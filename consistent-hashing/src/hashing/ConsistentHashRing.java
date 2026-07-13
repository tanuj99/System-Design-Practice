package hashing;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

public class ConsistentHashRing {

    private final int virtualNodes;

    /*
     * Hash Ring
     * 
     * hash ---> VirtualNode
     */
    private final SortedMap<Long, VirtualNode> ring = new TreeMap<>();

    public ConsistentHashRing(int virtualNodes) {
        this.virtualNodes = virtualNodes;
    }

    /**
     * Add a physical node to the ring.
     */
    public void addNode(String nodeId) {

        for (int i = 0; i < virtualNodes; i++) {

            VirtualNode vnode = new VirtualNode(nodeId, i);

            long hash = HashFunction.hash(vnode.toString());

            ring.put(hash, vnode);
        }
    }

    /**
     * Remove a physical node.
     */
    public void removeNode(String nodeId) {

        for (int i = 0; i < virtualNodes; i++) {

            VirtualNode vnode = new VirtualNode(nodeId, i);

            long hash = HashFunction.hash(vnode.toString());

            ring.remove(hash);
        }
    }

    /**
     * Returns the physical node responsible for the key.
     */
    public String getNode(String key) {

        if (ring.isEmpty()) {
            return null;
        }

        long hash = HashFunction.hash(key);

        /*
         * Find first node clockwise.
         */

        SortedMap<Long, VirtualNode> tailMap = ring.tailMap(hash);

        long nodeHash;

        if (tailMap.isEmpty()) {

            nodeHash = ring.firstKey();

        } else {

            nodeHash = tailMap.firstKey();
        }

        return ring.get(nodeHash)
                .getPhysicalNodeId();
    }

    public Collection<VirtualNode> getVirtualNodes() {
        return ring.values();
    }

    public void printRing() {

        System.out.println("\n===== HASH RING =====");

        for (Long hash : ring.keySet()) {

            System.out.println(hash + " -> " + ring.get(hash));
        }
    }
}