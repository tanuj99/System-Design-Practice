import model.User;
import repository.ShardedUserRepository;
import repository.UserRepository;
import shard.Shard;
import shard.ShardRouter;

public class Main {

    public static void main(String[] args) {

        /*
         * Create Shard Router
         */
        ShardRouter router = new ShardRouter(100);

        /*
         * Register Shards
         */
        router.addShard(new Shard("Shard-1"));
        router.addShard(new Shard("Shard-2"));
        router.addShard(new Shard("Shard-3"));

        /*
         * Create Repository
         */
        UserRepository repository = new ShardedUserRepository(router);

        /*
         * Create Users
         */
        User alice = new User("Alice", "alice@test.com");
        User bob = new User("Bob", "bob@test.com");
        User charlie = new User("Charlie", "charlie@test.com");
        User david = new User("David", "david@test.com");
        User eve = new User("Eve", "eve@test.com");

        /*
         * Save Users
         */
        repository.save(alice);
        repository.save(bob);
        repository.save(charlie);
        repository.save(david);
        repository.save(eve);

        System.out.println("\n========== USERS SAVED ==========\n");

        /*
         * Find Users
         */
        System.out.println(repository.findById(alice.getUserId()));
        System.out.println(repository.findById(bob.getUserId()));
        System.out.println(repository.findById(charlie.getUserId()));
        System.out.println(repository.findById(david.getUserId()));
        System.out.println(repository.findById(eve.getUserId()));

        /*
         * Print Distribution
         */
        System.out.println("\n========== SHARD DISTRIBUTION ==========");
        router.printDistribution();

        /*
         * Show Routing
         */
        System.out.println("\n========== USER ROUTING ==========");

        System.out.println(alice.getUsername() + " -> " + router.getShard(alice.getUserId()));

        System.out.println(bob.getUsername() + " -> " + router.getShard(bob.getUserId()));

        System.out.println(charlie.getUsername() + " -> " + router.getShard(charlie.getUserId()));

        System.out.println(david.getUsername() + " -> " + router.getShard(david.getUserId()));

        System.out.println(eve.getUsername() + " -> " + router.getShard(eve.getUserId()));

        /*
         * Add a New Shard
         */
        System.out.println("\n========== ADDING NEW SHARD ==========");

        router.addShard(new Shard("Shard-4"));

        /*
         * Routing after adding new shard
         */
        System.out.println("\n========== ROUTING AFTER ADDING SHARD ==========");

        System.out.println(alice.getUsername() + " -> " + router.getShard(alice.getUserId()));

        System.out.println(bob.getUsername() + " -> " + router.getShard(bob.getUserId()));

        System.out.println(charlie.getUsername() + " -> " + router.getShard(charlie.getUserId()));

        System.out.println(david.getUsername() + " -> " + router.getShard(david.getUserId()));

        System.out.println(eve.getUsername() + " -> " + router.getShard(eve.getUserId()));

        /*
         * Print New Distribution
         */
        System.out.println("\n========== UPDATED SHARD DISTRIBUTION ==========");
        router.printDistribution();

        System.out.println("\n========== DEMO COMPLETED ==========");
    }
}