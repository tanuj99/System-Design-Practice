import broker.EventBroker;
import broker.EventType;
import broker.PostCreatedEvent;
import consumer.FailingConsumer;

public class DLQTest {

    public static void main(String[] args) throws Exception {

        EventBroker broker = new EventBroker();
        broker.subscribe(EventType.POST_CREATED, new FailingConsumer());
        broker.publish(
                new PostCreatedEvent(
                        "P1",
                        "User1",
                        "Hello"));
        Thread.sleep(5000);
        broker.shutdown();
    }

}