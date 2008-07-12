package com.custardsource.cache.policy.simple;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Queue;
import java.util.SortedMap;
import java.util.TreeMap;

import com.custardsource.cache.policy.BaseCacheManager;
import com.custardsource.cache.policy.QueueAdapter;

public class LfuCacheManager<T> extends BaseCacheManager<T, LfuConfiguration> {
    private final SortedMap<Integer, Queue<T>> priorityQueue = new TreeMap<Integer, Queue<T>>();
    private final Map<T, Integer> visitCounts = new HashMap<T, Integer>();
    
    public LfuCacheManager(LfuConfiguration config) {
        setConfig(config);
    }
    
    public void add(T hit) {
        // TODO?
    }

    public int cacheSize() {
        return visitCounts.size();
    }

    public void clear() {
        visitCounts.clear();
        priorityQueue.clear();
    }

    public void remove(T hit) {
        Integer priority = visitCounts.remove(hit);
        if (priority != null) {
            priorityQueue.get(priority).remove();
        }
    }

    public void visit(T hit) {
        Integer visits = visitCounts.get(hit);
        boolean existed = (visits != null);
        if (existed) {
            Queue<T> oldVisited = priorityQueue.get(visits);
            oldVisited.remove(hit);
            if (oldVisited.isEmpty()) {
                priorityQueue.remove(visits);
            }
            visits++;
        } else {
            // It's new, make room if needed
            if (visitCounts.size() >= getConfig().getMaxNodes()) {
                Integer maxCount = priorityQueue.firstKey();
                Queue<T> queue = priorityQueue.get(maxCount);
                T evicted = queue.remove();
                if (queue.isEmpty()) {
                    priorityQueue.remove(maxCount);
                }
                visitCounts.remove(evicted);
                evict(evicted);
            }

            visits = 1;
        }
        
        Queue<T> newVisited = priorityQueue.get(visits);
        if (newVisited == null) {
            newVisited = new QueueAdapter<T>(new LinkedHashSet<T>());
            priorityQueue.put(visits, newVisited);
        }
        
        newVisited.add(hit);
        visitCounts.put(hit, visits);
        
    	if (!existed) {
            load(hit);
    	}
    }

	@Override
	protected String debugString() {
		return Arrays.toString(priorityQueue.entrySet().toArray());
	}
}