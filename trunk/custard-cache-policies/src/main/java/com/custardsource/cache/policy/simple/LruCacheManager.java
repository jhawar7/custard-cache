package com.custardsource.cache.policy.simple;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Queue;

import com.custardsource.cache.policy.BaseCacheManager;
import com.custardsource.cache.policy.QueueAdapter;

public class LruCacheManager<T> extends BaseCacheManager<T, LruConfiguration> {
    private final Queue<T> queue = new QueueAdapter<T>(new LinkedHashSet<T>());
    
    public LruCacheManager(LruConfiguration config) {
        setConfig(config);
    }
    
    public void add(T hit) {
        // TODO?
    }

    public int cacheSize() {
        return queue.size();
    }

    public void clear() {
        queue.clear();
    }

    public void remove(T hit) {
        queue.remove(hit);
    }

    public void visit(T hit) {
    	boolean existed = queue.remove(hit);
    	queue.add(hit);
        
    	if (!existed) {
            load(hit);
            if (queue.size() > getConfig().getMaxNodes()) {
                evict(queue.remove());
            }
    	}
    }

	@Override
	protected String debugString() {
		return Arrays.toString(queue.toArray());
	}
}