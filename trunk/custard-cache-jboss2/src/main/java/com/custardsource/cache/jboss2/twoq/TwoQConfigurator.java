package com.custardsource.cache.jboss2.twoq;

import com.aconex.cache.policy.twoq.TwoQConfiguration;
import com.custardsource.cache.jboss2.MultipleQueueConfigurator;

public class TwoQConfigurator extends MultipleQueueConfigurator<TwoQConfiguration> {
    @Override
    protected TwoQConfiguration createConfig() {
        return new TwoQConfiguration();
    }
}