package com.custardsource.cache.simulator.policy;

import java.text.NumberFormat;

import com.aconex.cache.policy.replacement.FixedReplacementPolicy;

public class FixedReplacementPolicySpecification extends StandardPolicySpecification {
    private final double t1Factor;
    private final NumberFormat formatter = NumberFormat.getPercentInstance();

    public FixedReplacementPolicySpecification(double t1Factor) {
        super(FixedReplacementPolicy.class);
        formatter.setMaximumFractionDigits(2);
        this.t1Factor = t1Factor;
    }

    @Override
    public String getExtraAttributes(int maxNodes) {
        return super.getExtraAttributes(maxNodes) + "<attribute name=\"t1Size\">"
                + getT1Size(maxNodes) + "</attribute>";
    }

    protected int getT1Size(int maxNodes) {
        return (int) (t1Factor * maxNodes);
    }

    @Override
    public String toString() {
        return super.toString() + " @ " + formatter.format(t1Factor) + " t1 size";
    }
}