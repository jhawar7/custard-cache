package com.custardsource.cache.policy;

public abstract class BasicConfiguration {
    private int maxNodes;
    
    public BasicConfiguration() {
    }

    public BasicConfiguration(int maxNodes) {
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
