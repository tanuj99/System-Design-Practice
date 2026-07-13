package replication;

import model.User;

public class LeaderFollowerTest {

    public static void main(String[] args) throws InterruptedException {

        LeaderFollowerDatabase database = new LeaderFollowerDatabase();

        User user = new User("Alice", "alice@test.com");

        System.out.println("\nWriting user...\n");

        database.save(user);

        System.out.println("\nReading immediately from Leader");

        System.out.println(database.readFromLeader(user.getUserId()));

        System.out.println("\nReading immediately from Follower");

        System.out.println(database.readFromFollower(user.getUserId()));

        System.out.println("\nWaiting for replication...\n");

        Thread.sleep(1500);

        System.out.println("Reading again from Follower");

        System.out.println(database.readFromFollower(user.getUserId()));

        database.shutdown();
    }
}