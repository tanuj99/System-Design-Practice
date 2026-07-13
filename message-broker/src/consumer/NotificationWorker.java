package consumer;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import broker.Event;
import broker.EventConsumer;
import broker.PostCreatedEvent;

public class NotificationWorker implements EventConsumer {

    private static final String FILE = "notifications.log";

    @Override
    public void consume(Event event) throws IOException {

        if (!(event instanceof PostCreatedEvent)) {
            return;
        }

        PostCreatedEvent postEvent = (PostCreatedEvent) event;

        try (PrintWriter writer = new PrintWriter(
                new FileWriter(FILE, true))) {

            writer.println("Notify Followers : " + postEvent.getUserId() + " created post " + postEvent.getPostId());
        }

        System.out.println("[Notification Worker] Notification Logged");
    }
}