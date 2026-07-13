import broker.EventBroker;
import broker.EventType;
import broker.PostCreatedEvent;
import consumer.FeedFanoutWorker;
import consumer.NotificationWorker;

public class App {
    public static void main(String[] args) throws Exception {
        EventBroker broker = new EventBroker();

        broker.subscribe(EventType.POST_CREATED, new FeedFanoutWorker());

        broker.subscribe(EventType.POST_CREATED, new NotificationWorker());

        PostCreatedEvent event = new PostCreatedEvent(
                "P101",
                "User1",
                "Hello World");
        broker.publish(event);
    }
}
