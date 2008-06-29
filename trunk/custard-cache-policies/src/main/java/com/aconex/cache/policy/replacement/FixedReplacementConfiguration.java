package com.aconex.cache.policy.replacement;

import com.aconex.cache.policy.MultipleQueueConfiguration;

public class FixedReplacementConfiguration extends MultipleQueueConfiguration {
    private int t1Size;

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