````markdown
# Scalable User Storage for 100M Users

## Overview

Designing a storage system for 100 million users requires a database architecture that is scalable, highly available, and fault tolerant. The system should efficiently support user registration, profile lookups, follow/unfollow operations, and authentication while maintaining low latency under heavy traffic. Since user data is highly structured and requires transactional guarantees, PostgreSQL is chosen as the primary database. To scale horizontally, the data is partitioned across multiple database shards using consistent hashing. Replication is used to improve availability and distribute read traffic.

---

# Functional Requirements

- Store user profiles
- Register new users
- Update profile information
- Retrieve user details
- Support follow and unfollow operations
- Handle 100 million users
- High availability
- Low latency reads

---

# Sharding Strategy

The users table is horizontally sharded using **user_id** as the sharding key. Each user is assigned to a shard through a Consistent Hash Ring with virtual nodes.

```
                 Consistent Hash Ring

                        User ID
                           |
                    Hash(user_id)
                           |
               +-----------+-----------+
               |           |           |
           Shard 1     Shard 2     Shard 3
```

Using consistent hashing ensures that when a new shard is added or removed, only a small percentage of users are redistributed instead of moving the entire dataset.

Example:

```
User 1001  -> Shard 1

User 2455  -> Shard 3

User 9876  -> Shard 2
```

### Why user_id?

- Uniform distribution
- Even storage utilization
- Easy routing
- Minimal hotspot creation
- Fast lookup using the primary key

---

# Replication Topology

Each shard follows a **Leader-Follower** replication model.

```
                   Shard 1

              +-------------+
              |   Leader    |
              +-------------+
               /           \
              /             \
     +-------------+   +-------------+
     | Follower A  |   | Follower B  |
     +-------------+   +-------------+

Writes  -> Leader

Reads   -> Followers
```

The leader accepts all write operations. Followers asynchronously replicate data from the leader and primarily serve read requests. If the leader fails, one follower is promoted as the new leader.

Advantages:

- High availability
- Read scalability
- Fault tolerance
- Reduced load on the leader

---

# Consistency Choice

The system uses **strong consistency for writes** and **eventual consistency for read replicas**.

Critical operations such as user registration, password updates, and profile modifications always write to the leader. Read requests are served from follower replicas to improve throughput.

This introduces a short replication delay.

Example:

```
Update Username

↓

Leader Updated

↓

Follower Updated after ~1 second
```

A user refreshing their profile immediately after an update may briefly see the old username if the request is served by a follower. This trade-off is acceptable because it improves read scalability while maintaining correctness for writes.

---

# Hot Shard Mitigation

Although consistent hashing distributes users evenly, certain users may receive significantly more traffic than others.

To reduce hot shards, the following strategies are used:

### Read Replicas

Popular user profiles are served from multiple follower replicas instead of the leader.

### Cache Layer

Frequently accessed user profiles are cached in Redis to reduce database load.

```
Client

↓

Redis

↓

Database
```

### Load Balancer

Read requests are distributed across multiple replicas to prevent any single database from becoming overloaded.

### Virtual Nodes

Each physical shard owns multiple virtual nodes on the consistent hash ring, resulting in a more balanced distribution of users when shards are added or removed.

---

# Handling a Celebrity with 50 Million Followers

A celebrity account generates significantly more traffic than a normal user.

Problems include:

- Millions of profile reads
- Massive follower queries
- Extremely high fan-out when publishing new posts

The following techniques are used to handle celebrity users.

### Aggressive Caching

Celebrity profiles are cached in Redis with a long Time-To-Live (TTL). Most profile requests are served directly from the cache instead of the database.

### Separate Followers Table

Followers are stored in a dedicated `follows` table instead of embedding follower lists within the user record. This allows follower relationships to scale independently.

```
Followers

--------------------------

celebrity_id

follower_id
```

### Fan-out on Read

Instead of pushing every new post to 50 million follower feeds, the system computes the celebrity's posts during feed generation. This avoids overwhelming the system with write amplification.

### Pagination

Follower lists are retrieved using pagination rather than loading all followers into memory.

Example:

```
GET /followers?page=1&limit=100
```

### Read Replication

Follower queries are served by multiple read replicas to distribute traffic.

---

# Database Schema

```
Users
------
user_id (PK)
username
email
created_at

Follows
--------
follower_id
followee_id

Posts
-----
post_id
user_id
content
created_at
```

The `Users` table is sharded by `user_id`, while related entities such as `Posts` and `Follows` use the same routing strategy to keep related data colocated whenever possible.

---

# High-Level Architecture

```
                  Clients
                     |
             Load Balancer
                     |
               API Gateway
                     |
             User Service
                     |
             Shard Router
                     |
          Consistent Hash Ring
         /         |          \
        /          |           \
   Shard 1     Shard 2      Shard 3
      |            |             |
 Leader/Follower Leader/Follower Leader/Follower
      |
     Redis Cache
```

The Shard Router determines the correct shard using consistent hashing. The User Service performs writes on the leader and routes read requests to follower replicas. Redis serves frequently accessed user profiles, reducing database load.

---

# Conclusion

The proposed architecture scales horizontally by sharding users across multiple database servers using consistent hashing with virtual nodes. Leader-follower replication improves availability and read scalability, while Redis caching minimizes database load for frequently accessed profiles. Strong consistency is maintained for writes, whereas follower replicas provide eventual consistency for read operations. Hot shards are mitigated through caching, read replicas, load balancing, and virtual nodes. Celebrity users are handled using fan-out on read, aggressive caching, pagination, and scalable follower storage. This design enables the platform to efficiently support over 100 million users while maintaining high availability, low latency, and operational scalability.
````
