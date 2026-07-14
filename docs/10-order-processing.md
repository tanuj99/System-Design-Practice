# Order Processing System for E-commerce

## Overview

An e-commerce order processing system must reliably handle millions of orders while maintaining data consistency, fault tolerance, and scalability. During peak sales events such as Black Friday or Amazon Prime Day, thousands of orders may be placed every second. The system should process orders without losing data, ensure payment reliability, update inventory accurately, notify customers, and generate analytics. To achieve these goals, the architecture combines synchronous communication for critical operations with asynchronous event-driven processing for non-critical tasks.

---

# High-Level Architecture

<img width="1361" height="623" alt="image" src="https://github.com/user-attachments/assets/f938ec33-3d04-413c-a7be-43cdb2a62ad8" />


The **Order Service** is responsible for creating orders and coordinating downstream services. Payment validation is performed synchronously because the user must know immediately whether the payment succeeded. Once payment is successful, an `OrderCreated` event is published to Kafka. Independent workers process inventory updates, shipping, notifications, analytics, and search indexing asynchronously.

---

# Synchronous vs Asynchronous Boundaries

Not every operation should be asynchronous. The system separates operations based on business criticality.

### Synchronous Operations

The following operations are executed synchronously:

- User authentication
- Payment authorization
- Order validation
- Inventory reservation

These operations directly affect whether an order can be placed successfully. If payment fails or inventory is unavailable, the user must receive an immediate response.

Flow:

```text
Client
   |
Order Service
   |
Payment Service
   |
Inventory Service
   |
Order Confirmed
```

### Asynchronous Operations

After the order is successfully created, several secondary operations are triggered asynchronously.

These include:

- Sending email notifications
- Sending SMS notifications
- Updating recommendation systems
- Updating analytics dashboards
- Search indexing
- Loyalty point calculation

Flow:

<img width="744" height="331" alt="image" src="https://github.com/user-attachments/assets/87346770-afdb-41fb-9294-e1f51ed6e2cf" />


This significantly reduces user response time because the Order Service does not wait for these tasks to complete.

---

# Queue Technology Choice

The system uses **Apache Kafka** as the message broker.

Reasons for choosing Kafka:

- High throughput suitable for thousands of orders per second.
- Durable message storage with configurable retention.
- Supports multiple consumer groups consuming the same event independently.
- Excellent horizontal scalability through partitions.
- Allows replaying events during recovery or rebuilding downstream systems.

Each order generates a single `OrderCreated` event.

Consumer Groups:

```
OrderCreated Topic

-------------------------

Inventory Worker

Shipping Worker

Notification Worker

Analytics Worker

Search Worker
```

Each consumer group processes the same event independently without affecting the others.

---

# Delivery Semantics

Different operations require different delivery guarantees.

### Payment Processing – Exactly Once

Payment processing must use **Exactly Once** semantics.

Charging a customer twice or losing a successful payment is unacceptable.

Implementation:

- Transactional database writes.
- Idempotency keys.
- Kafka transactional producer.
- Idempotent payment service.

---

### Inventory Updates – At Least Once

Inventory updates should use **At Least Once** delivery.

If an inventory event is processed twice, the inventory service uses an idempotency key to ignore duplicate updates. Missing an inventory update is worse than processing it twice.

Implementation:

- Retry on failure.
- Event ID stored in a deduplication table.
- Ignore duplicate event IDs.

---

### Analytics – At Most Once

Analytics events may use **At Most Once** delivery.

Occasionally missing an analytics event has little business impact, whereas duplicate events can distort business reports.

Implementation:

- Fire-and-forget event publishing.
- No retries.

---

# Retry Strategy

Consumers may fail because of temporary issues such as network outages or downstream service failures.

The system retries failed events using exponential backoff.

Retry policy:

```
Attempt 1

↓

Wait 1 second

↓

Attempt 2

↓

Wait 2 seconds

↓

Attempt 3

↓

Dead Letter Queue
```

Only transient failures are retried. Permanent failures such as invalid data are sent directly to the Dead Letter Queue.

---

# Dead Letter Queue (DLQ)

Events that cannot be processed after three retry attempts are moved to a Dead Letter Queue.

```
Kafka Topic

↓

Consumer

↓

Failure

↓

Retry

↓

Retry

↓

Retry

↓

Dead Letter Queue
```

Examples of events sent to the DLQ:

- Invalid order data
- Corrupted event payload
- Permanent payment validation failure
- Missing inventory records

Operations teams can inspect DLQ messages, fix the underlying issue, and replay the events if necessary.

The DLQ prevents a single faulty event from blocking the processing of subsequent events.

---

# Backpressure Handling During Sales Spikes

During large sales events, producers may generate events faster than consumers can process them.

Example:

```
Producer

15,000 Orders/sec

↓

Kafka

↓

Inventory Worker

5,000 Orders/sec
```

Without backpressure, consumer memory usage would continuously grow, eventually causing failures.

The system uses the following strategies.

### Consumer Scaling

Increase the number of consumer instances in the consumer group.

```
Inventory Group

Inventory Worker 1

Inventory Worker 2

Inventory Worker 3

Inventory Worker 4
```

Kafka automatically redistributes partitions among available consumers.

---

### Bounded Queue

Each worker maintains a bounded in-memory queue.

When the queue becomes full, new requests are temporarily rejected until capacity becomes available.

This prevents excessive memory usage.

---

### Rate Limiting

The producer temporarily slows event publication when consumer lag exceeds a configured threshold.

This protects downstream systems from overload.

---

### Load Shedding

Non-critical events such as recommendation updates or analytics may be dropped during extreme traffic conditions, while critical events such as payments and inventory updates continue to be processed.

---

# Failure Recovery

The system is designed to recover automatically from service failures.

- Kafka persists events on disk until successfully processed.
- Consumers resume processing from the last committed offset.
- Leader-follower replication protects Kafka brokers from hardware failures.
- Idempotent consumers safely handle duplicate deliveries.
- Failed events are isolated in the Dead Letter Queue.

This ensures that no confirmed orders are lost even during service outages.

---

# Advantages of the Design

- Fast user response through asynchronous processing.
- Reliable payment handling with exactly-once semantics.
- High throughput using Kafka partitions and consumer groups.
- Independent scaling of downstream services.
- Fault tolerance through retries and dead-letter queues.
- Durable event storage and replay capability.
- Protection against traffic spikes through backpressure mechanisms.

---

# Conclusion

The proposed architecture combines synchronous processing for business-critical operations with asynchronous event-driven communication for downstream workflows. Apache Kafka serves as the central event backbone, enabling scalable and reliable communication between services. Exactly-once delivery guarantees are applied to payment processing, while inventory updates use at-least-once semantics with idempotent consumers. Automatic retries, dead-letter queues, and backpressure mechanisms ensure resilience during failures and high-traffic events. This architecture provides the scalability, reliability, and fault tolerance required to process millions of orders while maintaining a responsive user experience.
