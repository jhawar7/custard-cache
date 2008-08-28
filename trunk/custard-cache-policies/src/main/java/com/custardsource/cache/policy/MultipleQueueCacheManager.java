package com.custardsource.cache.policy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.custardsource.cache.util.LogUtils;

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
public abstract class MultipleQueueCacheManager<T, U extends BasicConfiguration> extends BaseCacheManager<T, U> {
    private static final Log LOG = LogFactory.getLog(BaseCacheManager.class);

    private Map<Queue<T>, String> queueNames = new IdentityHashMap<Queue<T>, String>();
    private List<Queue<T>> queuesInOrder = new LinkedList<Queue<T>>();
    private Map<T, Queue<T>> currentLocations = new HashMap<T, Queue<T>>();

    public MultipleQueueCacheManager() {
    }

    public void clear() {
        currentLocations.clear();
    }

    public void add(T hit) {
        // TODO we don't do anything here because a get it always followed by a visit -- what if
        // it's inserted manually? Hmm.
    }

    public void remove(T hit) {
        Queue<T> currentLocation = currentLocations.get(hit);
        if (currentLocation != null) {
            currentLocation.remove(hit);
            currentLocations.remove(hit);
            afterRemove(hit);
        }
    }

    public void visit(T hit) {
        LogUtils.debug(LOG, "Visited: %s", hit);
        Queue<T> currentLocation = currentLocations.get(hit);

        if (currentLocation != null) {
            LogUtils.debug(LOG, " found in %s", queueName(currentLocation));
            onHit(hit, currentLocation);
        } else {
            LogUtils.debug(LOG, " new node load");
            onMiss(hit);
        }
        dumpStatus();
        assertInvariants();
    }

    protected void insertNode(T entry, Queue<T> destination) {
        LogUtils.debug(LOG, " insert %s to %s", entry, queueName(destination));
        destination.add(entry);
        currentLocations.put(entry, destination);
        afterInsert(entry, null, destination);
    }

    /**
     * Invoked after a node has been inserted into one of the queues (this may be a new entry, a
     * move between queues, or a removal-and-reinsert to the same queue). This gives subclasses a
     * chance to do any bookkeeping required based on the new destination queue (e.g. let the
     * listeners know that this counts a 'load')
     */
    protected abstract void afterInsert(T entry, Queue<T> source, Queue<T> destination);

    /**
     * Invoked after a node has been removed for good (that is, it's not being tracked in any way,
     * including in a 'ghost' queue.
     */
    protected void afterRemove(T entry) {
    }

    protected String queueName(Queue<T> queue) {
        String name = queueNames.get(queue);
        return (name == null) ? "(unknown)" : name;
    }

    protected void moveNode(T entry, Queue<T> currentLocation, Queue<T> to) {
        LogUtils.debug(LOG, " move node %s from %s to %s", entry, queueName(currentLocation),
                queueName(to));
        currentLocation.remove(entry);
        to.add(entry);
        currentLocations.put(entry, to);
        afterInsert(entry, currentLocation, to);
    }

    protected void evictNode(Queue<T> from) {
        LogUtils.debug(LOG, " evict head from %s", queueName(from));
        T node = from.remove();
        if (node != null) {
            evict(node);
            currentLocations.remove(node);
            afterRemove(node);
        }
    }

    protected void removeNode(Queue<T> from) {
        LogUtils.debug(LOG, " remove already-evicted head from %s", queueName(from));
        T node = from.remove();
        if (node != null) {
            currentLocations.remove(node);
            afterRemove(node);
        }
    }

    protected abstract void onHit(T entry, Queue<T> currentLocation);

    protected abstract void onMiss(T entry);

    protected abstract void assertInvariants();

    protected void assertInvariant(boolean b) {
        // Disable for now - not sure if we want this at all but useful during testing
        // throw new IllegalStateException("invariant failed");
        if (!b) {
            LOG.error("Invariant failed!", new Exception());
        }
    }

    protected abstract void dumpStatus();

    protected String dumpCapacity(Queue<T> queue) {
        return queueName(queue) + ":" + queue.size();
    }

    protected String dumpQueue(Queue<T> queue) {
        StringBuilder builder = new StringBuilder();
        builder.append(queueName(queue));
        builder.append(StringUtils.leftPad("(" + queue.size() + ")", 6));
        builder.append(": [");
        for (T nodeEntry : queue) {
            builder.append(StringUtils.right(StringUtils.rightPad(nodeEntry.toString(), 4), 4));
            builder.append(" ");
        }
        builder.append("]");
        return builder.toString();
    }

    protected T moveHead(Queue<T> from, Queue<T> to) {
        T item = from.remove();
        to.add(item);
        currentLocations.put(item, to);
        return item;
    }

    protected final void registerQueue(Queue<T> queue, String name) {
        queueNames.put(queue, name);
        queuesInOrder.add(queue);
    }

    @Override
    protected String debugString() {
        StringBuilder builder = new StringBuilder();
        for (Queue<T> queue : queuesInOrder) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(queueName(queue) + ":" + Arrays.toString(queue.toArray()));
        }
        return builder.toString();
    }
}