package broker;

import java.util.ArrayList;
import java.util.List;

public class DeadLetterQueue {

    private final List<Event> failedEvents = new ArrayList<>();

    public void add(Event event) {
        failedEvents.add(event);
        System.out.println("Event moved to DLQ : " + event.getEventId());
    }

    public List<Event> getFailedEvents() {
        return failedEvents;
    }

}