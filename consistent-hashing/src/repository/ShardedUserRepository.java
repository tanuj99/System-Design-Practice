package repository;

import model.User;
import shard.Shard;
import shard.ShardRouter;

public class ShardedUserRepository implements UserRepository {

    private final ShardRouter shardRouter;

    public ShardedUserRepository(ShardRouter shardRouter) {
        this.shardRouter = shardRouter;
    }

    @Override
    public void save(User user) {

        Shard shard = shardRouter.getShard(user.getUserId());

        shard.saveUser(user);
    }

    @Override
    public User findById(String userId) {

        Shard shard = shardRouter.getShard(userId);

        return shard.getUser(userId);
    }

    @Override
    public void delete(String userId) {

        Shard shard = shardRouter.getShard(userId);

        shard.deleteUser(userId);
    }
}