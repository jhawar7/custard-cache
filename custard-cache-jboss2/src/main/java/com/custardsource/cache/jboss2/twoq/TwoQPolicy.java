package com.custardsource.cache.jboss2.twoq;

import org.jboss.cache.eviction.EvictionException;
import org.jboss.cache.eviction.EvictionQueue;
import org.jboss.cache.eviction.NodeEntry;
import org.jboss.cache.eviction.Region;

import com.custardsource.cache.jboss2.CacheManagerEvictionAlgorithm;
import com.custardsource.cache.jboss2.CacheManagerEvictionPolicy;
import com.custardsource.cache.jboss2.CacheManagerEvictionQueue;
import com.custardsource.cache.policy.twoq.TwoQCacheManager;

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