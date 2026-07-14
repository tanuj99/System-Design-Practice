package broker.repository;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class EventDedupRepository {

    private final Set<String> processedEvents = ConcurrentHashMap.newKeySet();

    /**
     * @return true if event was processed for the first time.
     */
    public boolean markProcessed(String eventId) {
        return processedEvents.add(eventId);
    }

    public boolean isProcessed(String eventId) {
        return processedEvents.contains(eventId);
    }

    public int size() {
        return processedEvents.size();
    }
}