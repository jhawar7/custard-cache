package com.aconex.cache.simulator.policy;

import org.jboss.cache.eviction.EvictionPolicy;

public class StandardPolicySpecification implements PolicySpecification {
    private String policy;

    public StandardPolicySpecification(String policy) {
        this.policy = policy;
    }

    public StandardPolicySpecification(Class<? extends EvictionPolicy> policyClass) {
        this(policyClass.getName());
    }

    public String toString() {
        return policy;
    }

    public String getPolicy() {
        return policy;
    }

    public String getExtraAttributes(int maxNodes) {
        return "<attribute name=\"minNodes\">" + getMinNodes(maxNodes) + "</attribute>";
    }

    protected int getMinNodes(int maxNodes) {
        return maxNodes;
    }

}