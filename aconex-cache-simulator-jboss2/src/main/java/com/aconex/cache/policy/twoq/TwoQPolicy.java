package com.aconex.cache.policy.twoq;

import org.jboss.cache.eviction.EvictionException;
import org.jboss.cache.eviction.EvictionQueue;
import org.jboss.cache.eviction.NodeEntry;
import org.jboss.cache.eviction.Region;

import com.aconex.cache.policy.CacheManagerEvictionAlgorithm;
import com.aconex.cache.policy.CacheManagerEvictionPolicy;
import com.aconex.cache.policy.CacheManagerEvictionQueue;

public class TwoQPolicy extends CacheManagerEvictionPolicy {

    @Override
    protected CacheManagerEvictionAlgorithm newAlgorithm() {
        return new CacheManagerEvictionAlgorithm() {
            @Override
            protected EvictionQueue setupEvictionQueue(Region arg0) throws EvictionException {
                TwoQConfigurator configurator = (TwoQConfigurator) region
                        .getEvictionConfiguration();
                return new CacheManagerEvictionQueue(new TwoQCacheManager<NodeEntry>(configurator
                        .getConfig()));
            }
        };
    }

    public Class<?> getEvictionConfigurationClass() {
        return TwoQConfigurator.class;
    }
}