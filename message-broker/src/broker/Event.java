package broker;

import java.time.Instant;
import java.util.UUID;

public abstract class Event {

    private final String eventId;

    private final EventType eventType;

    private final long createdAt;

    protected Event(EventType eventType) {

        this.eventId = UUID.randomUUID().toString();

        this.eventType = eventType;

        this.createdAt = Instant.now().toEpochMilli();
    }

    public String getEventId() {
        return eventId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public long getCreatedAt() {
        return createdAt;
    }

}