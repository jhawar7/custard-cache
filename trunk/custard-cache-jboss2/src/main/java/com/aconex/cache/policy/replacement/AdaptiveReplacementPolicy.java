package com.aconex.cache.policy.replacement;

import org.jboss.cache.eviction.EvictionException;
import org.jboss.cache.eviction.EvictionQueue;
import org.jboss.cache.eviction.NodeEntry;
import org.jboss.cache.eviction.Region;

import com.aconex.cache.policy.CacheManagerEvictionAlgorithm;
import com.aconex.cache.policy.CacheManagerEvictionPolicy;
import com.aconex.cache.policy.CacheManagerEvictionQueue;

public class AdaptiveReplacementPolicy extends CacheManagerEvictionPolicy {
    @Override
    protected CacheManagerEvictionAlgorithm newAlgorithm() {
        return new CacheManagerEvictionAlgorithm() {
            @Override
            protected EvictionQueue setupEvictionQueue(Region arg0) throws EvictionException {
                AdaptiveReplacementConfigurator config = (AdaptiveReplacementConfigurator) region
                        .getEvictionConfiguration();
                return new CacheManagerEvictionQueue(
                        new AdaptiveReplacementCacheManager<NodeEntry>(config.getConfig()));
            }
        };
    }

    public Class<?> getEvictionConfigurationClass() {
        return AdaptiveReplacementConfigurator.class;
    }
}
