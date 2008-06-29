package com.aconex.cache.policy.replacement;

import com.aconex.cache.policy.MultipleQueueConfigurator;


public class AdaptiveReplacementConfigurator extends MultipleQueueConfigurator<AdaptiveReplacementConfiguration> {
    @Override
    protected AdaptiveReplacementConfiguration createConfig() {
        return new AdaptiveReplacementConfiguration();
    }
}