package broker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventBroker {

    private final Map<EventType, List<EventConsumer>> subscribers = new HashMap<>();

    public void subscribe(EventType eventType, EventConsumer consumer) {
        subscribers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(consumer);
    }

    public void publish(Event event) {

        List<EventConsumer> consumers = subscribers.get(event.getEventType());

        if (consumers == null) {
            return;
        }

        for (EventConsumer consumer : consumers) {
            try {
                consumer.consume(event);
            } catch (Exception ex) {
                System.out.println("Consumer Failed : " + ex.getMessage());
            }
        }
    }
}