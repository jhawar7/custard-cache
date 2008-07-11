package com.custardsource.cache.policy.simple;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Queue;

import com.custardsource.cache.policy.BaseCacheManager;
import com.custardsource.cache.policy.QueueAdapter;

public class FifoCacheManager<T> extends BaseCacheManager<T, FifoConfiguration> {
    private final Queue<T> queue = new QueueAdapter<T>(new LinkedHashSet<T>());
    
    public FifoCacheManager(FifoConfiguration config) {
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
        if (!queue.contains(hit)) {
            queue.add(hit);
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
