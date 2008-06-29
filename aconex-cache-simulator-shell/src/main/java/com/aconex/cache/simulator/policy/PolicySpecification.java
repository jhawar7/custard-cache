package com.aconex.cache.simulator.policy;

public interface PolicySpecification {
    String getPolicy();
    String getExtraAttributes(int maxNodes);
}