package shard;

import java.util.HashMap;
import java.util.Map;

import model.User;

public class Shard {

    private final String shardId;

    private final Map<String, User> users;

    public Shard(String shardId) {
        this.shardId = shardId;
        this.users = new HashMap<>();
    }

    public String getShardId() {
        return shardId;
    }

    public void saveUser(User user) {
        users.put(user.getUserId(), user);
    }

    public User getUser(String userId) {
        return users.get(userId);
    }

    public boolean containsUser(String userId) {
        return users.containsKey(userId);
    }

    public void deleteUser(String userId) {
        users.remove(userId);
    }

    public int size() {
        return users.size();
    }

    @Override
    public String toString() {
        return shardId;
    }
}