package hashing;

public class VirtualNode {

    private final String physicalNodeId;
    private final int replicaIndex;

    public VirtualNode(String physicalNodeId, int replicaIndex) {
        this.physicalNodeId = physicalNodeId;
        this.replicaIndex = replicaIndex;
    }

    public String getPhysicalNodeId() {
        return physicalNodeId;
    }

    public int getReplicaIndex() {
        return replicaIndex;
    }

    @Override
    public String toString() {
        return physicalNodeId + "#" + replicaIndex;
    }
}