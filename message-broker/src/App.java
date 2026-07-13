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

        for (int i = 1; i <= 10; i++) {
            broker.publish(new PostCreatedEvent(
                    "P" + i,
                    "User1",
                    "Hello"));
        }
        Thread.sleep(3000);
        broker.shutdown();
    }
}
