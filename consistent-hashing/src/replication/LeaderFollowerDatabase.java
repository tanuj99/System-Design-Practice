package replication;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import model.User;

public class LeaderFollowerDatabase {

    /*
     * Simulated Databases
     */
    private final Map<String, User> leaderDB = new HashMap<>();
    private final Map<String, User> followerDB = new HashMap<>();

    /*
     * Simulates asynchronous replication
     */
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    /**
     * All writes go to leader.
     */
    public void save(User user) {

        leaderDB.put(user.getUserId(), user);

        System.out.println("Leader: User written -> " + user.getUsername());

        /*
         * Replicate after 1 second
         */
        executor.schedule(() -> {

            followerDB.put(user.getUserId(), user);

            System.out.println("Follower: Replicated -> " + user.getUsername());

        }, 1, TimeUnit.SECONDS);
    }

    /**
     * Read from Leader.
     */
    public User readFromLeader(String userId) {
        return leaderDB.get(userId);
    }

    /**
     * Read from Follower.
     */
    public User readFromFollower(String userId) {
        return followerDB.get(userId);
    }

    public void shutdown() {
        executor.shutdown();
    }
}