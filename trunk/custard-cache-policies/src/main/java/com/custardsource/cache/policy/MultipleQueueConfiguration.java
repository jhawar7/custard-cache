package com.custardsource.cache.policy;

public abstract class MultipleQueueConfiguration {
    private int maxNodes;
    
    public MultipleQueueConfiguration() {
    }

    public MultipleQueueConfiguration(int maxNodes) {
    	this.maxNodes = maxNodes;
    }

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
