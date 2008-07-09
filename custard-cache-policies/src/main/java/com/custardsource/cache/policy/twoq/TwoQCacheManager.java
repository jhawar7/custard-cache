package com.custardsource.cache.policy.twoq;

import java.util.LinkedHashSet;
import java.util.Queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.custardsource.cache.policy.MultipleQueueCacheManager;
import com.custardsource.cache.policy.QueueAdapter;
import com.custardsource.cache.util.LogUtils;

/**
 * Cache eviction policy which implements the '2Q' algorithm by Theodore Johnson and Dennis Shasha.
 * This is a non-priority-queue-based variant of an LRU/2 algorithm; rather than keeping records of
 * reference times, 2 queues are kept, one for seen-once-only items and one for items seen multiple
 * times. A third queue is held of items which have been evicted but are still being 'watched'.
 * 
 * @see <a href="http://db.cs.berkeley.edu/cs262/2q.pdf"><cite>2Q: A Low Overhead High Performance
 *      Buffer Management Replacement Algorithm</cite></a>, Theodore Johnson and Dennis Shasha
 * @author pcowan
 */
public class TwoQCacheManager<T> extends MultipleQueueCacheManager<T> {
    private static final Log LOG = LogFactory.getLog(MultipleQueueCacheManager.class);

    protected Queue<T> am = new QueueAdapter<T>(new LinkedHashSet<T>());
    protected Queue<T> a1In = new QueueAdapter<T>(new LinkedHashSet<T>());
    protected Queue<T> a1Out = new QueueAdapter<T>(new LinkedHashSet<T>());
    private final TwoQConfiguration config;

    public TwoQCacheManager(TwoQConfiguration config) {
        this.config = config;
        registerQueue(am, "am");
        registerQueue(a1In, "a1In");
        registerQueue(a1Out, "a1Out");
    }

    @Override
    public void clear() {
        super.clear();
        am.clear();
        a1In.clear();
        a1Out.clear();
    }

    @Override
    protected void afterInsert(T entry, Queue<T> source, Queue<T> destination) {
        if (destination == am || destination == a1In) {
            // TODO check the 'from' here?
            load(entry);
        }
    }

    public int cacheSize() {
        return am.size() + a1In.size();
    }

    @Override
    public void assertInvariants() {
        assertInvariant(0 <= cacheSize());
        assertInvariant(a1In.size() <= cacheCapacity());
        assertInvariant(am.size() <= cacheCapacity());
        assertInvariant(cacheSize() <= cacheCapacity());
    }

    @Override
    protected void dumpStatus() {
        if (LOG.isTraceEnabled()) {
            // TODO make these one message
            LOG.trace("  kIn/total split: " + kIn() + "/" + cacheCapacity());
            LOG.trace("  kOut: " + kOut());
            LOG.trace("  " + dumpQueue(am));
            LOG.trace("  " + dumpQueue(a1In));
            LOG.trace("  " + dumpQueue(a1Out));
        } else if (LOG.isDebugEnabled()) {
            LOG.debug("  kIn/total " + kIn() + "/" + cacheCapacity() + ", kOut " + kOut()
                    + ", actual capacities " + dumpCapacity(am) + " " + dumpCapacity(a1In) + " "
                    + dumpCapacity(a1Out) + " ");
        }
    }

    private int kIn() {
        // TODO make configurable
        return cacheCapacity() / 4;
    }

    private int kOut() {
        // TODO make configurable
        return cacheCapacity() / 2;
    }

    protected int cacheCapacity() {
        return config.getMaxNodes();
    }

    @Override
    protected void onMiss(T entry) {
        reclaimForEntry();
        insertNode(entry, a1In);
    }

    private void reclaimForEntry() {
        if (cacheSize() < cacheCapacity()) {
            // Do nothing, we're fine
            LogUtils.debug(LOG, " got room, we're fine");
        } else if (a1In.size() > kIn()) {
            LogUtils.debug(LOG, " a1In is full; move to a1Out");
            evict(moveHead(a1In, a1Out));
            if (a1Out.size() > kOut()) {
                LogUtils.debug(LOG, " a1Out is now full; drop oldest");
                evictNode(a1Out);
            }
        } else {
            LogUtils.debug(LOG, " a1Out has room; evict from am");
            evictNode(am);
        }
    }

    @Override
    protected void onHit(T node, Queue<T> currentLocation) {
        if (currentLocation == am) {
            LogUtils.debug(LOG, " Shuffling to head of am");
            moveNode(node, currentLocation, am);
        } else if (currentLocation != a1In) {
            // Must be in a1Out
            reclaimForEntry();
            moveNode(node, currentLocation, am);
        }
    }
}