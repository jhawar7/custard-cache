package com.custardsource.cache.policy.twoq;

import com.custardsource.cache.policy.BasicConfiguration;

public class TwoQConfiguration extends BasicConfiguration {
    private float recentHitRatio = 0.3f;
    private float evictedTrackingRatio = 0.5f;

    public TwoQConfiguration() {
        super();
    }

    public TwoQConfiguration(int maxNodes, float recentHitRatio) {
        super(maxNodes);
        this.recentHitRatio = recentHitRatio;
    }

    public TwoQConfiguration(int maxNodes, float recentHitRatio, float evictedTrackingRatio) {
        this(maxNodes, recentHitRatio);
        this.evictedTrackingRatio = evictedTrackingRatio;
    }

    public float getRecentHitRatio() {
        return recentHitRatio;
    }

    public void setRecentHitRatio(float ratio) {
        this.recentHitRatio = ratio;
    }

    public float getEvictedTrackingRatio() {
        return evictedTrackingRatio;
    }

    public void getEvictedTrackingRatio(float ratio) {
        this.evictedTrackingRatio = ratio;
    }

    public String toString() {
        return super.toString() + ", recentHitRatio = " + recentHitRatio
                + ", evictedTrackingRatio = " + evictedTrackingRatio;
    }
}