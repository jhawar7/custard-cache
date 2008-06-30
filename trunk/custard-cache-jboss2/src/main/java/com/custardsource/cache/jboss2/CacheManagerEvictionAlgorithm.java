package com.custardsource.cache.jboss2;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.cache.Fqn;
import org.jboss.cache.eviction.BaseEvictionAlgorithm;
import org.jboss.cache.eviction.EvictionException;
import org.jboss.cache.eviction.NodeEntry;

import com.custardsource.cache.util.LogUtils;

public abstract class CacheManagerEvictionAlgorithm extends BaseEvictionAlgorithm {
    private static final Log LOG = LogFactory.getLog(CacheManagerEvictionAlgorithm.class);

    @Override
    protected void prune() throws EvictionException {
        LogUtils.debug(LOG, "Pruning");
        super.prune();
        ((CacheManagerEvictionQueue) evictionQueue).dumpStatus();
    }

    protected void processVisitedNodes(Fqn fqn) throws EvictionException {
        super.processVisitedNodes(fqn);
        ((CacheManagerEvictionQueue) evictionQueue).processVisit(fqn);
    }

    @Override
    protected boolean shouldEvictNode(NodeEntry ne) {
        boolean result = ((CacheManagerEvictionQueue) evictionQueue).containsNodeEntry(ne);
        LogUtils.debug(LOG, " Checking for evict %s - %s", ne.getFqn(), result);
        return result;
    }

    protected void evict(NodeEntry ne) {
        if (ne != null) {
            ((CacheManagerEvictionQueue) evictionQueue).removeNodeEntry(ne);
            if (!this.evictCacheNode(ne.getFqn())) {
                try {
                    recycleQueue.put(ne);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
