package com.custardsource.cache.jboss2.replacement;

import org.jboss.cache.eviction.EvictionException;
import org.jboss.cache.eviction.EvictionQueue;
import org.jboss.cache.eviction.NodeEntry;
import org.jboss.cache.eviction.Region;

import com.custardsource.cache.jboss2.CacheManagerEvictionAlgorithm;
import com.custardsource.cache.jboss2.CacheManagerEvictionPolicy;
import com.custardsource.cache.jboss2.CacheManagerEvictionQueue;
import com.custardsource.cache.policy.replacement.AdaptiveReplacementCacheManager;

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
