package broker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventBroker {

    private final Map<EventType, List<EventConsumer>> subscribers = new HashMap<>();

    /*
     * Bounded Queue (Backpressure)
     */
    private final BlockingQueue<EventTask> queue = new ArrayBlockingQueue<>(100);

    /*
     * Worker Threads
     */
    private final ExecutorService workers = Executors.newFixedThreadPool(4);

    public EventBroker() {

        for (int i = 0; i < 4; i++) {
            workers.submit(() -> {
                while (true) {
                    try {
                        EventTask task = queue.take();
                        process(task);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
        }
    }

    public void subscribe(EventType type, EventConsumer consumer) {
        subscribers.computeIfAbsent(type, k -> new ArrayList<>()).add(consumer);
    }

    public void publish(Event event) {

        List<EventConsumer> consumers = subscribers.get(event.getEventType());

        if (consumers == null) {
            return;
        }
        for (EventConsumer consumer : consumers) {
            EventTask task = new EventTask(event, consumer);

            /*
             * Backpressure
             */

            boolean accepted = queue.offer(task);

            if (!accepted) {
                System.out.println("Queue Full. Dropping Event " + event.getEventId());
            }
        }
    }

    private void process(EventTask task) {
        try {
            task.consumer.consume(task.event);
        } catch (Exception e) {
            System.out.println("Consumer " + task.consumer.getConsumerName() + " failed.");
        }
    }

    public void shutdown() {
        workers.shutdown();
    }

    /*
     * Internal wrapper
     */

    private static class EventTask {

        private final Event event;

        private final EventConsumer consumer;

        public EventTask(Event event, EventConsumer consumer) {
            this.event = event;
            this.consumer = consumer;
        }
    }
}