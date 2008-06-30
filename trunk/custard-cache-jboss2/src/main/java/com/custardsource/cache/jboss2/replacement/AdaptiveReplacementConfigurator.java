package com.custardsource.cache.jboss2.replacement;

import com.custardsource.cache.jboss2.MultipleQueueConfigurator;
import com.custardsource.cache.policy.replacement.AdaptiveReplacementConfiguration;


public class AdaptiveReplacementConfigurator extends MultipleQueueConfigurator<AdaptiveReplacementConfiguration> {
    @Override
    protected AdaptiveReplacementConfiguration createConfig() {
        return new AdaptiveReplacementConfiguration();
    }
}