package com.custardsource.cache.policy.replacement;

import java.util.LinkedHashSet;
import java.util.Queue;

import com.custardsource.cache.policy.BasicConfiguration;
import com.custardsource.cache.policy.CacheManager;
import com.custardsource.cache.policy.MultipleQueueCacheManager;
import com.custardsource.cache.policy.QueueAdapter;

/**
 * Base class for the {@link CacheManager} implementation of 'Replacement Cache' algorithms of
 * Nimrod Megiddo and Dharmendra S. Modha of IBM. These caches keep 2 queues, t1 and t2, which
 * together comprise the cache. Subclasses implement the breakdown between the two groups
 * differently.
 */
public abstract class ReplacementCacheManager<T, U extends BasicConfiguration> extends MultipleQueueCacheManager<T, U> {
    protected Queue<T> t1 = new QueueAdapter<T>(new LinkedHashSet<T>());
    protected Queue<T> t2 = new QueueAdapter<T>(new LinkedHashSet<T>());

    public ReplacementCacheManager() {
        registerQueue(t1, "t1");
        registerQueue(t2, "t2");
    }

    @Override
    public void clear() {
        super.clear();
        t1.clear();
        t2.clear();
    }

    public int size() {
        return t1.size() + t2.size();
    }

    protected abstract int cacheCapacity();

    @Override
    protected void afterInsert(T entry, Queue<T> source, Queue<T> destination) {
		if ((destination == t1 || destination == t2)
				&& !(source == t1 || source == t2)) {
			// Anything which WAS due for eviction is now part of the cache
			// proper, and should no longer be evicted.
			load(entry);
		}
	}

    public int cacheSize() {
        return t1.size() + t2.size();
    }
}