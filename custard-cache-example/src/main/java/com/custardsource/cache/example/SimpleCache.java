package com.custardsource.cache.example;

import java.util.HashMap;
import java.util.Map;

import com.custardsource.cache.policy.CacheManager;
import com.custardsource.cache.policy.CacheManagerListener;

public class SimpleCache<K, V> {
    private final CacheManager<K> policy;
	private final CacheManagerListener<K> listener;
	private final Map<K, V> cache = new HashMap<K, V>();

	public SimpleCache(CacheManager<K> policy) {
		this.policy = policy;
		listener = new SimpleListener();
		this.policy.addListener(listener);
	}
	
	public void add(K key, V value) {
	    if (cache.put(key, value) == null) {
	        policy.add(key);
	    }
	    policy.visit(key);
	}

   public V get(K key) {
        V result = cache.get(key);
        if (result != null) {
            policy.visit(key);
        }
        return result;
    }

	public void remove(K key) {
	    cache.remove(key);
	    policy.remove(key);
	}
	
    private class SimpleListener implements CacheManagerListener<K> {
        public void objectLoaded(K key) {
        }

        public void objectReadyForEviction(K key) {
            remove(key);
        }

    }
}