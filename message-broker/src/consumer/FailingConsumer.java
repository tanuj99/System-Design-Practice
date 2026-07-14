package consumer;

import broker.Event;
import broker.EventConsumer;

public class FailingConsumer implements EventConsumer {

    @Override
    public void consume(Event event) throws Exception {
        throw new RuntimeException("Intentional Failure");
    }

    @Override
    public String getConsumerName() {
        return "Failing Consumer";
    }
}