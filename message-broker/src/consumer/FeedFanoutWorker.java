package consumer;

import broker.Event;
import broker.EventConsumer;
import broker.PostCreatedEvent;

public class FeedFanoutWorker implements EventConsumer {

    @Override
    public void consume(Event event) {

        if (!(event instanceof PostCreatedEvent)) {
            return;
        }

        PostCreatedEvent postEvent = (PostCreatedEvent) event;

        System.out.println("[Feed Worker] Updating feed cache for followers of user : " + postEvent.getUserId());
        /*
         * Simulate Feed Cache Update
         */
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {
            // do nothing
        }
        System.out.println("[Feed Worker] Feed cache updated for post : " + postEvent.getPostId());
    }
}