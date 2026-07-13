package shard;

import java.util.HashMap;
import java.util.Map;

import hashing.ConsistentHashRing;

public class ShardRouter {

    private final ConsistentHashRing hashRing;

    private final Map<String, Shard> shards;

    public ShardRouter(int virtualNodes) {

        this.hashRing = new ConsistentHashRing(virtualNodes);

        this.shards = new HashMap<>();
    }

    public void addShard(Shard shard) {

        shards.put(shard.getShardId(), shard);

        hashRing.addNode(shard.getShardId());
    }

    public void removeShard(String shardId) {

        shards.remove(shardId);

        hashRing.removeNode(shardId);
    }

    public Shard getShard(String userId) {

        String shardId = hashRing.getNode(userId);

        return shards.get(shardId);
    }

    public void printDistribution() {

        System.out.println("\nShard Distribution");

        for (Shard shard : shards.values()) {

            System.out.println(
                    shard.getShardId()
                            + " -> "
                            + shard.size()
                            + " users");
        }
    }
}