package com.aconex.cache.simulator.policy;

import java.text.NumberFormat;

public class MinNodeRatioPolicySpecification extends StandardPolicySpecification {
    private final double minNodeFactor;

    private final static NumberFormat FORMATTER = NumberFormat.getPercentInstance();
    static {
        FORMATTER.setMaximumFractionDigits(2);
    }

    public MinNodeRatioPolicySpecification(String policy, double minNodeFactor) {
        super(policy);
        this.minNodeFactor = minNodeFactor;
    }

    @Override
    protected int getMinNodes(int maxNodes) {
        return (int) (minNodeFactor * maxNodes);
    }

    @Override
    public String toString() {
        return super.toString() + " @ " + FORMATTER.format(minNodeFactor) + " minNodes";
    }
}