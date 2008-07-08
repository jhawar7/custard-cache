package com.custardsource.cache.policy.simple;

import com.custardsource.cache.policy.BasicConfiguration;

public class LruConfiguration extends BasicConfiguration {
    public LruConfiguration() {
    }

    public LruConfiguration(int maxNodes) {
        super(maxNodes);
    }
}
