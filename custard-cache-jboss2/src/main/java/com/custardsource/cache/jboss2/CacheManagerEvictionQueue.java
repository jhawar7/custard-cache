package com.custardsource.cache.jboss2;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.cache.Fqn;
import org.jboss.cache.eviction.EvictionQueue;
import org.jboss.cache.eviction.NodeEntry;

import com.custardsource.cache.policy.CacheManager;
import com.custardsource.cache.policy.CacheManagerListener;
import com.custardsource.cache.policy.QueueAdapter;
import com.custardsource.cache.util.LogUtils;

/**
 * <p>
 * This is the key class for all JBoss cache implementations based in a {@link CacheManager} (see
 * {@link CacheManagerEvictionPolicy}). JBoss operates on the theory that there is a single 'queue'
 * comprising all objects in the cache region, ordered from 'next-to-evict' to 'last-to-evict'. This
 * isn't really true in the case of many multiple-queue algorithms; they have a fixed set of objects
 * which will not be evicted (for now), and then another set (represented by the 'toEvict' field of
 * this class) which are safe to evict; these classes only ever hand out the 'toEvict' objects for
 * eviction, meaning that the size of the 'evictionqueue' doesn't really reflect the number of
 * objects available for eviction.
 * </p>
 * <p>
 * Note that the 'toEvict' objects may still be kept in the cache in some form (e.g. as references
 * for later hit-counting) but the underlying node in the cache can happily be thrown out.
 * </p>
 * <p>
 * This munging of the EvictionQueue is a bit odd, and explains why some of the methods in this
 * class might look a little bit odd. They work, but may be rather fragile and may prove prone to
 * breakage with later jboss-cache versions.
 * </p>
 * 
 * @author pcowan
 */
public class CacheManagerEvictionQueue implements EvictionQueue, CacheManagerListener<NodeEntry> {
    private static final Log LOG = LogFactory.getLog(CacheManagerEvictionQueue.class);

    private Queue<NodeEntry> toEvict = new QueueAdapter<NodeEntry>(new LinkedHashSet<NodeEntry>());
    private Map<String, NodeEntry> byFqn = new HashMap<String, NodeEntry>();
    private int numberOfElements = 0;

    private final CacheManager<NodeEntry> cacheManager;

    public CacheManagerEvictionQueue(CacheManager<NodeEntry> cacheManager) {
        cacheManager.addListener(this);
        this.cacheManager = cacheManager;
    }

    public void clear() {
        byFqn.clear();
        toEvict.clear();
        cacheManager.clear();
        numberOfElements = 0;
    }

    public NodeEntry getFirstNodeEntry() {
        return toEvict.peek();
    }

    public NodeEntry getNodeEntry(Fqn fqn) {
        return this.getNodeEntry(fqn.toString());
    }

    public NodeEntry getNodeEntry(String fqn) {
        return byFqn.get(fqn);
    }

    public void removeNodeEntry(NodeEntry entry) {
        byFqn.remove(entry);
        this.cacheManager.remove(entry);
        toEvict.remove(entry);
    }

    public void addNodeEntry(NodeEntry entry) {
        byFqn.put(entry.getFqn().toString(), entry);
        this.cacheManager.add(entry);
        numberOfElements += entry.getNumberOfElements();
    }

    public void processVisit(Fqn fqn) {
        NodeEntry entry = getNodeEntry(fqn);
        if (entry == null) {
            entry = new NodeEntry(fqn);
        }
        processVisit(entry);
    }

    private void processVisit(NodeEntry entry) {
        this.cacheManager.visit(entry);
    }

    public boolean containsNodeEntry(NodeEntry entry) {
        return toEvict.contains(entry);
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public void modifyElementCount(int arg0) {
        numberOfElements += arg0;
    }

    public int getNumberOfNodes() {
        return toEvict.size();
    }

    public Iterator<NodeEntry> iterate() {
        return toEvict.iterator();
    }

    public void objectReadyForEviction(NodeEntry item) {
        toEvict.add(item);
    }

    public void objectLoaded(NodeEntry item) {
        toEvict.remove(item);
    }

    public void dumpStatus() {
        LogUtils.debug(LOG, " Eviction queue %s", toEvict);
    }
}
