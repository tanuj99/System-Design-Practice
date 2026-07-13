package broker;

public interface EventConsumer {
    void consume(Event event) throws Exception;
}