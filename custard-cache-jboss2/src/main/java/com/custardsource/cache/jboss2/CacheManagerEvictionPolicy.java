package com.custardsource.cache.jboss2;

import org.jboss.cache.TreeCache;
import org.jboss.cache.eviction.BaseEvictionPolicy;
import org.jboss.cache.eviction.EvictionAlgorithm;
import org.jboss.cache.eviction.RegionManager;

import com.custardsource.cache.jboss2.CacheManagerEvictionAlgorithm;

/**
 * The base class for all 'multiple-queue' algorithm implementations. These are not to be confused
 * with the specific algorithm called the 'Multi-Queue Algorithm', which is one specific example of
 * a multiple-queue algorithm (and which is known here by its common name, 'MQ', to slightly ease
 * confusion). These algorithms all work by maintaining multiple queues of cached nodes and
 * shuffling items between the various queues in order to evict based on e.g. number of accesses
 * without having to maintain expensive priority queues.
 * 
 * @author pcowan
 */
public abstract class CacheManagerEvictionPolicy extends BaseEvictionPolicy {
    protected RegionManager regionManager_;
    private CacheManagerEvictionAlgorithm algorithm;

    public CacheManagerEvictionPolicy() {
        algorithm = newAlgorithm();
    }

    protected abstract CacheManagerEvictionAlgorithm newAlgorithm();

    public final EvictionAlgorithm getEvictionAlgorithm() {
        return algorithm;
    }

    public final void configure(TreeCache cache) {
        super.configure(cache);
        regionManager_ = cache_.getEvictionRegionManager();
    }

}
