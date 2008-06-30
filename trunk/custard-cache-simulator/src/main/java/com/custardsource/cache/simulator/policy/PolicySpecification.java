package com.custardsource.cache.simulator.policy;

public interface PolicySpecification {
    String getPolicy();
    String getExtraAttributes(int maxNodes);
}