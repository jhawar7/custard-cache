package com.custardsource.cache.jboss2.replacement;

import com.aconex.cache.policy.replacement.AdaptiveReplacementConfiguration;
import com.custardsource.cache.jboss2.MultipleQueueConfigurator;


public class AdaptiveReplacementConfigurator extends MultipleQueueConfigurator<AdaptiveReplacementConfiguration> {
    @Override
    protected AdaptiveReplacementConfiguration createConfig() {
        return new AdaptiveReplacementConfiguration();
    }
}