package com.aconex.cache.policy.twoq;

import com.aconex.cache.policy.MultipleQueueConfigurator;

public class TwoQConfigurator extends MultipleQueueConfigurator<TwoQConfiguration> {
    @Override
    protected TwoQConfiguration createConfig() {
        return new TwoQConfiguration();
    }
}