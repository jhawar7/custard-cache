package com.custardsource.cache.policy.simple;

import com.custardsource.cache.policy.BasicConfiguration;

public class LfuConfiguration extends BasicConfiguration {
    public LfuConfiguration() {
    }

    public LfuConfiguration(int maxNodes) {
        super(maxNodes);
    }
}
