package com.custardsource.cache.policy.mq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.custardsource.cache.policy.MultipleQueueCacheManager;
import com.custardsource.cache.policy.QueueAdapter;

/**
 * Cache eviction policy which implements the MQ (Multi-Queue Replacement) algorithm by Yuanyuan
 * Zhou, James F Philbin, and Kai Li. This algorithm partitions the cache space into a number of
 * queues; entries are promoted to 'higher' queues after multiple accesses, and 'demoted' to lower
 * queues if they do not have repeated access after a particular number of 'clock ticks'.
 * 
 * @see <a href="http://www.usenix.org/event/usenix01/full_papers/zhou/zhou_html/index.html"><cite>The
 *      Multi-Queue Replacement Algorithm for Second Level Buffer Caches</cite></a>, Yuanyuan
 *      Zhou, James F Philbin, and Kai Li
 * @author pcowan
 */
public class MQCacheManager<T> extends MultipleQueueCacheManager<T, MQConfiguration> {
    private static final Log LOG = LogFactory.getLog(MultipleQueueCacheManager.class);

    private ArrayList<Queue<T>> queues = new ArrayList<Queue<T>>();
    private Queue<T> qOut = new QueueAdapter<T>(new LinkedHashSet<T>());
    private Map<T, MQMetadata> metadata = new HashMap<T, MQMetadata>();
    private int currentTime = 0;

    public MQCacheManager(MQConfiguration config) {
        for (int i = 0; i < config.getQueueCount(); i++) {
            Queue<T> queue = new QueueAdapter<T>(new LinkedHashSet<T>());
            queues.add(queue);
            registerQueue(queue, "Q" + i);
        }
        registerQueue(qOut, "qOut");
        setConfig(config);
    }

    @Override
    protected void afterInsert(T entry, Queue<T> source, Queue<T> destination) {
        if (destination != qOut && (source == qOut || source == null)) {
            load(entry);
        }
    }

    @Override
    public void assertInvariants() {
        // TODO not sure what these are
    }

    @Override
    protected void dumpStatus() {
        if (LOG.isTraceEnabled()) {
            StringBuilder builder = new StringBuilder();
            builder.append("  current time: " + currentTime + "\n");
            for (Queue<T> queue : queues) {
                builder.append("  " + dumpQueue(queue) + "\n");
            }
            builder.append("  " + dumpQueue(qOut));
            LOG.trace(builder.toString());
        } else if (LOG.isDebugEnabled()) {
            StringBuilder builder = new StringBuilder();
            builder.append("  time " + currentTime + ", actual capacities ");
            for (Queue<T> queue : queues) {
                builder.append("  " + dumpCapacity(queue) + ", ");
            }
            builder.append(dumpCapacity(qOut));
            LOG.debug(builder);
        }
    }

    @Override
    protected void onHit(T entry, Queue<T> currentLocation) {
        MQMetadata metadata = getMetadata(entry);
        metadata.logVisit();
        if (currentLocation == qOut) {
            freeUpSpace();
        }
        moveNode(entry, currentLocation, getDesiredQueue(metadata));
        metadata.markAsModified();
        checkForDemotions();
    }

    private void checkForDemotions() {
        currentTime++;
        for (int i = 1; i < queues.size(); i++) {
            T entry = queues.get(i).peek();
            if (entry != null) {
                MQMetadata metadata = getMetadata(entry);
                if (metadata.isExpired()) {
                    moveNode(entry, queues.get(i), queues.get(i - 1));
                    metadata.markAsModified();
                }
            }
        }
    }

    private MQMetadata getMetadata(T entry) {
        MQMetadata data = metadata.get(entry);
        if (data == null) {
            data = new MQMetadata();
            metadata.put(entry, data);
        }
        return data;
    }

    @Override
    protected void onMiss(T entry) {
        freeUpSpace();
        MQMetadata metadata = getMetadata(entry);
        metadata.numberOfNodeVisits = 1;
        metadata.markAsModified();
        insertNode(entry, getDesiredQueue(metadata));
        checkForDemotions();
    }

    private void freeUpSpace() {
        if (cacheSize() < cacheCapacity()) {
            return;
        }
        Queue<T> queue = getFirstNonEmptyQueue();
        if (qOut.size() >= qOutMax()) {
            afterRemove(qOut.remove());
        }
        evict(moveHead(queue, qOut));
    }

    private int qOutMax() {
        // TODO make this configurable
        return getConfig().getMaxNodes() / 2;
    }

    private Queue<T> getFirstNonEmptyQueue() {
        for (Queue<T> queue : queues) {
            if (!queue.isEmpty()) {
                return queue;
            }
        }
        return null;
    }

    public int cacheSize() {
        int size = 0;
        for (Queue<T> queue : queues) {
            size += queue.size();
        }
        return size;
    }

    private Queue<T> getDesiredQueue(MQMetadata metadata) {
        return queues.get(getQueueNumber(metadata));
    }

    private int getQueueNumber(MQMetadata metadata) {
        return Math.min((int) logToBase(2, metadata.numberOfNodeVisits), queues.size() - 1);
    }

    private double logToBase(double base, double value) {
        // Seriously, this isn't in java.lang.Math? Or commons-math?
        return Math.log(value) / Math.log(base);
    }

    protected int cacheCapacity() {
        return getConfig().getMaxNodes();
    }

    private class MQMetadata {
        private int numberOfNodeVisits;
        private int expiryTime;

        public void logVisit() {
            numberOfNodeVisits++;
        }

        public void markAsModified() {
            expiryTime = currentTime + getConfig().getLifetime();
        }
        
        public boolean isExpired() {
            return expiryTime < currentTime;
        }
    }

    @Override
    public void clear() {
        super.clear();
        for (Queue<T> queue : queues) {
            queue.clear();
        }
        qOut.clear();
        metadata.clear();
        currentTime = 0;
    }

    @Override
    protected void afterRemove(T entry) {
        super.afterRemove(entry);
        metadata.remove(entry);
    }
}
