package com.custardsource.cache.jboss2.replacement;

import org.jboss.cache.eviction.EvictionException;
import org.jboss.cache.eviction.EvictionQueue;
import org.jboss.cache.eviction.NodeEntry;
import org.jboss.cache.eviction.Region;

import com.aconex.cache.policy.replacement.FixedReplacementCacheManager;
import com.custardsource.cache.jboss2.CacheManagerEvictionAlgorithm;
import com.custardsource.cache.jboss2.CacheManagerEvictionPolicy;
import com.custardsource.cache.jboss2.CacheManagerEvictionQueue;

public class FixedReplacementPolicy extends CacheManagerEvictionPolicy {
    @Override
    protected CacheManagerEvictionAlgorithm newAlgorithm() {
        return new CacheManagerEvictionAlgorithm() {
            @Override
            protected EvictionQueue setupEvictionQueue(Region arg0) throws EvictionException {
                FixedReplacementConfigurator config = (FixedReplacementConfigurator) region
                        .getEvictionConfiguration();
                return new CacheManagerEvictionQueue(new FixedReplacementCacheManager<NodeEntry>(
                        config.getConfig()));
            }
        };
    }

    public Class<?> getEvictionConfigurationClass() {
        return FixedReplacementConfigurator.class;
    }
}
