package com.aconex.cache.policy;

public abstract class MultipleQueueConfiguration {
    private int maxNodes;

    public int getMaxNodes() {
        return maxNodes;
    }

    public void setMaxNodes(int maxNodes) {
        this.maxNodes = maxNodes;
    }

    public String toString() {
        // TODO - raise bug on incorrect message in the FIFO version of this
        return this.getClass().getSimpleName() + ": maxNodes = " + getMaxNodes();
    }
}
