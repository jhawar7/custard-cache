package com.custardsource.cache.jboss2.twoq;

import com.custardsource.cache.jboss2.MultipleQueueConfigurator;
import com.custardsource.cache.policy.twoq.TwoQConfiguration;

public class TwoQConfigurator extends MultipleQueueConfigurator<TwoQConfiguration> {
    @Override
    protected TwoQConfiguration createConfig() {
        return new TwoQConfiguration();
    }
}