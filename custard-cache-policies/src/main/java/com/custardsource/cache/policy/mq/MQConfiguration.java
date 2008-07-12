package com.custardsource.cache.policy.mq;

import com.custardsource.cache.policy.BasicConfiguration;

public class MQConfiguration extends BasicConfiguration {
	private int queueCount = 3;
    private int lifetime = 3;
    
    public MQConfiguration() {
		super();
	}

	public MQConfiguration(int maxNodes) {
		super(maxNodes);
	}

	public MQConfiguration(int maxNodes, int queueCount, int lifetime) {
		super(maxNodes);
		this.queueCount = queueCount;
		this.lifetime = lifetime;
	}

    @Override
    public String toString() {
        return super.toString() + ", queueCount=" + queueCount + ", lifetime=" + lifetime;
    }

    public int getQueueCount() {
        return queueCount;
    }

    // TODO is there any sensible way to adjust this dynamically?
    // TODO shouldn't be public
    public void setQueueCount(int queueCount) {
        if (queueCount <= 1) { 
            throw new IllegalArgumentException("queueCount must be > 1");
        }
        this.queueCount = queueCount;
    }

    public int getLifetime() {
        return lifetime;
    }

    // TODO is there any sensible way to adjust this dynamically?
    // TODO shouldn't be public
    public void setLifetime(int lifetime) {
        if (lifetime <= 1) { 
            throw new IllegalArgumentException("lifetime must be >= 1");
        }
        this.lifetime = lifetime;
    }
}
