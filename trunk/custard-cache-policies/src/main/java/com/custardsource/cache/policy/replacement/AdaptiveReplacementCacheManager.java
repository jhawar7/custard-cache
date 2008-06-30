package com.custardsource.cache.policy.replacement;

import java.util.LinkedHashSet;
import java.util.Queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.custardsource.cache.policy.QueueAdapter;
import com.custardsource.cache.util.LogUtils;

/**
 * Cache eviction policy which implements the Adaptive Replacement Cache algorithm by Nimrod Megiddo
 * and Dharmendra S. Modhain of IBM. The algorithm partitions the cache space into two queues: hit
 * items are originally loaded into 't1' (for recently-hit items), once they are hit again they move
 * into 't2' (for frequently-hit items). Items dropping out of these lists fall into 'directory'
 * lists (b1, b2); these items are evicted from the cache proper but information about them is kept
 * around until the directory is also full. Hits in these additional regions are used to dynamically
 * tune the t1:t2 balance to reflect the bias of the current workload towards recently-used (t1) or
 * frequently-used (t2) items. The Fixed Replacement cache (as implemented in
 * {@link FixedReplacementCacheManager}) is a static version of this, where the t1:t2 mix is set by
 * the user.
 * 
 * @see <a
 *      href="http://www.almaden.ibm.com/storagesystems/projects/arc/">http://www.almaden.ibm.com/storagesystems/projects/arc/</a>
 * @see <a href="http://www.almaden.ibm.com/cs/people/dmodha/ARC.pdf"><cite>Outperforming LRU with
 *      an Adaptive Replacement Cache Algorithm</cite></a>, Nimrod Megiddo and Dharmendra S.
 *      Modhain
 * @see <a href="www.usenix.org/publications/login/2003-08/pdfs/Megiddo.pdf"><cite>One Up On LRU</cite></a>,
 *      Nimrod Megiddo and Dharmendra S. Modhain, <cite>;login:</cite> August 2003
 * @see <a href="http://www.almaden.ibm.com/storagesystems/projects/arc/arcfast.pdf"><cite>ARC: A
 *      Self-Tuning, Low Overhead Replacement Cache</cite></a>, Nimrod Megiddo and Dharmendra S.
 *      Modhain
 * @see <a href="http://en.wikipedia.org/wiki/Adaptive_Replacement_Cache"><cite>Adaptive
 *      Replacement Cache</cite></a> on Wikipedia
 * @see FixedReplacementCacheAlgorithm
 * @author pcowan
 */
public class AdaptiveReplacementCacheManager<T> extends ReplacementCacheManager<T> {
    private static final Log LOG = LogFactory.getLog(AdaptiveReplacementCacheManager.class);
    private Queue<T> b1 = new QueueAdapter<T>(new LinkedHashSet<T>());
    private Queue<T> b2 = new QueueAdapter<T>(new LinkedHashSet<T>());
    private final AdaptiveReplacementConfiguration config;
    private int targetT1Size = 0;

    public AdaptiveReplacementCacheManager(AdaptiveReplacementConfiguration config) {
        this.config = config;
        registerQueue(b1, "b1");
        registerQueue(b2, "b2");
    }

    @Override
    public void clear() {
        super.clear();
        b1.clear();
        b2.clear();
    }

    private int directorySize() {
        return t1.size() + b1.size() + t2.size() + b2.size();
    }

    @Override
    public void assertInvariants() {
        assertInvariant(0 <= leftSideSize());
        assertInvariant(leftSideSize() <= cacheCapacity());
        assertInvariant(0 <= directorySize());
        assertInvariant(directorySize() <= directoryCapacity());
    }

    private int directoryCapacity() {
        return config.getMaxNodes() * 2;
    }

    private int leftSideSize() {
        return t1.size() + b1.size();
    }

    @Override
    protected void dumpStatus() {
        if (LOG.isTraceEnabled()) {
            // TODO make these one message
            LOG.trace("  Target t1 size: " + targetT1Size);
            LOG.trace("  " + dumpQueue(t1));
            LOG.trace("  " + dumpQueue(b1));
            LOG.trace("  " + dumpQueue(t2));
            LOG.trace("  " + dumpQueue(b2));
        } else if (LOG.isDebugEnabled()) {
            LOG.debug("  Target t1 size: " + targetT1Size + ", actual capacities "
                    + dumpCapacity(t1) + " " + dumpCapacity(t2) + " " + dumpCapacity(b1) + " "
                    + dumpCapacity(b2));
        }
    }

    @Override
    protected int cacheCapacity() {
        return config.getMaxNodes();
    }

    @Override
    protected void onMiss(T entry) {
        if (leftSideSize() >= cacheCapacity()) {
            LogUtils.debug(LOG, " Side 1 full ");
            if (t1.size() < cacheCapacity()) {
                LogUtils.debug(LOG, " b1 contains item, evicting");
                evictNode(b1);
                freeRoom(null);
            } else {
                LogUtils.debug(LOG, " b1's empty, evict from t1");
                evictNode(t1);
            }
        } else {
            if (directorySize() >= cacheCapacity()) {
                LogUtils.debug(LOG, " directory full");
                if (directorySize() >= directoryCapacity()) {
                    LogUtils.debug(LOG, " both sides full, evict from b2");
                    evictNode(b2);
                }
                freeRoom(null);
            } else {
                LogUtils.debug(LOG, " room in the cache, straight to t1");
            }
        }
        insertNode(entry, t1);
    }

    private void freeRoom(Queue<T> hitList) {
        if (!t1.isEmpty()
                && (t1.size() > targetT1Size || (t1.size() == targetT1Size && hitList == b2))) {
            LogUtils.debug(LOG, " doReplace -- t1 -> b1");
            evict(moveHead(t1, b1));
        } else {
            if (t2.isEmpty()) {
                LogUtils.debug(LOG, " doReplace -- t2 -> b2, but t2 is empty");
            } else {
                LogUtils.debug(LOG, " doReplace -- t2 -> b2");
                evict(moveHead(t2, b2));
            }
        }
    }

    @Override
    protected void onHit(T node, Queue<T> currentLocation) {
        if (currentLocation == t1 || currentLocation == t2) {
            LogUtils.debug(LOG, " Moving from %s to head of t2", queueName(currentLocation));
            moveNode(node, currentLocation, t2);
        } else {
            // It's b1 or b2
            LogUtils.debug(LOG, " Moving from %s to head of t2", queueName(currentLocation));
            if (currentLocation == b1) {
                // B1 hit - favour recency
                LogUtils.debug(LOG, "  B1 hit - favour recency");
                targetT1Size = Math.min(targetT1Size + Math.max(b2.size() / b1.size(), 1),
                        cacheCapacity());
            } else {
                // B2 hit - favour frequency
                LogUtils.debug(LOG, "  B2 hit - favour frequency");
                targetT1Size = Math.max(targetT1Size - Math.max(b1.size() / b2.size(), 1), 0);
            }
            freeRoom(currentLocation);
            moveNode(node, currentLocation, t2);
        }
    }

}
