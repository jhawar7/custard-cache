package com.custardsource.cache.policy.simple;

import com.custardsource.cache.policy.BasicConfiguration;

public class FifoConfiguration extends BasicConfiguration {
    public FifoConfiguration() {
    }

    public FifoConfiguration(int maxNodes) {
        super(maxNodes);
    }
}
