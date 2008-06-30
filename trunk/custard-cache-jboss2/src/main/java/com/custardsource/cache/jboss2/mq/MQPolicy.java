package com.custardsource.cache.jboss2.mq;

import org.jboss.cache.eviction.EvictionException;
import org.jboss.cache.eviction.EvictionQueue;
import org.jboss.cache.eviction.NodeEntry;
import org.jboss.cache.eviction.Region;

import com.aconex.cache.policy.mq.MQCacheManager;
import com.custardsource.cache.jboss2.CacheManagerEvictionAlgorithm;
import com.custardsource.cache.jboss2.CacheManagerEvictionPolicy;
import com.custardsource.cache.jboss2.CacheManagerEvictionQueue;

public class MQPolicy extends CacheManagerEvictionPolicy {
    @Override
    protected CacheManagerEvictionAlgorithm newAlgorithm() {
        return new CacheManagerEvictionAlgorithm() {
            @Override
            protected EvictionQueue setupEvictionQueue(Region arg0) throws EvictionException {
                MQConfigurator configurator = (MQConfigurator) region.getEvictionConfiguration();
                return new CacheManagerEvictionQueue(new MQCacheManager<NodeEntry>(configurator.getConfig()));
            }
        };
    }

    public Class<?> getEvictionConfigurationClass() {
        return MQConfigurator.class;
    }
}