package broker;

public class PostCreatedEvent extends Event {

    private final String postId;

    private final String userId;

    private final String content;

    public PostCreatedEvent(String postId,
            String userId,
            String content) {

        super(EventType.POST_CREATED);

        this.postId = postId;
        this.userId = userId;
        this.content = content;
    }

    public String getPostId() {
        return postId;
    }

    public String getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {

        return "PostCreatedEvent{" +
                "eventId='" + getEventId() + '\'' +
                ", postId='" + postId + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}