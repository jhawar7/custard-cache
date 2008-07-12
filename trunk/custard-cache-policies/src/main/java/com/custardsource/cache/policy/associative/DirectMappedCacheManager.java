package com.custardsource.cache.policy.associative;

import java.util.Arrays;

import com.custardsource.cache.policy.BaseCacheManager;

/**
 * Implementation of a direct-mapped associative cache, as might be used in a CPU cache. Note that
 * this is more of an academic exercise than anything else; this is not a particularly useful
 * algorithm as the job of a CacheManager is to merely call back which 'keys' should be evicted; if
 * the actual cache (e.g. the thing that holds the data corresponding to the key) has to do a
 * non-trivial eviction process then the enormous speed advantages of Direct Mapping are wasted. 
 */
public class DirectMappedCacheManager<T> extends BaseCacheManager<T, DirectMappedConfiguration<? super T>> {
    private T[] storage;
    private int size = 0;
    
    @SuppressWarnings("unchecked")
    public DirectMappedCacheManager(DirectMappedConfiguration<? super T> config) {
        setConfig(config);
        storage = (T[]) new Object[config.getMaxNodes()];
    }
    
    public void add(T hit) {
        // TODO?
    }

    public int cacheSize() {
        return size;
    }

    @SuppressWarnings("unchecked")
    public void clear() {
        storage = (T[]) new Object[getConfig().getMaxNodes()];
        size = 0;
    }

    public void remove(T hit) {
        int location = getConfig().getAssociator().calculateLocation(hit) % storage.length;
        if (hit.equals(storage[location])) {
            storage[location] = null;
            size--;
        }
    }

    public void visit(T hit) {
        int location = getConfig().getAssociator().calculateLocation(hit) % storage.length;
        if (hit.equals(storage[location])) {
            return;
        }
        
        if (storage[location] == null) {
            size++;
        } else {
            evict(storage[location]);
        }
        storage[location] = hit;
        load(hit);
    }

	@Override
	protected String debugString() {
		return Arrays.toString(storage);
	}
}