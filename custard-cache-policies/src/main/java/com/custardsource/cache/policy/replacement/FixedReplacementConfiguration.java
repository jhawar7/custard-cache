package com.custardsource.cache.policy.replacement;

import com.custardsource.cache.policy.BasicConfiguration;

public class FixedReplacementConfiguration extends BasicConfiguration {
    private int t1Size;

    public FixedReplacementConfiguration() {
		super();
	}

	public FixedReplacementConfiguration(int maxNodes, int t1Size) {
		super(maxNodes);
		this.t1Size = t1Size;
	}

	public FixedReplacementConfiguration(int maxNodes, double t1Percent) {
		this(maxNodes, (int) t1Percent * maxNodes);
	}

	public int getT1Size() {
        return t1Size;
    }

    public void setT1Size(int fixedT1Size) {
        this.t1Size = Math.max(fixedT1Size, 0);
    }

    public String toString() {
        return super.toString() + ", t1Size = " + getT1Size();
    }
}