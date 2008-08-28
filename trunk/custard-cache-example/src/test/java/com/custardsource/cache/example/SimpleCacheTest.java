package com.custardsource.cache.example;

import com.custardsource.cache.policy.simple.LruCacheManager;
import com.custardsource.cache.policy.simple.LruConfiguration;

import junit.framework.TestCase;

public class SimpleCacheTest extends TestCase {
    // Simple integration test because I'm lazy
    public void testSimpleCache() {
        SimpleCache<Integer, String> cache = new SimpleCache<Integer, String>(new LruCacheManager<Integer>(
                new LruConfiguration(3)));
        cache.add(1, "a");
        cache.add(2, "bb");
        cache.add(3, "ccc");
        assertEquals("a", cache.get(1));
        assertEquals("bb", cache.get(2));
        assertEquals("ccc", cache.get(3));
        // state: 1 2 3
        cache.add(4, "dddd");
        // state: 2 3 4
        assertNull(cache.get(1));
        assertEquals("bb", cache.get(2));
        // state: 3 4 2
        cache.remove(3);
        // state: 4 2
        cache.add(5, "eeeee");
        // state: 4 2 5
        assertEquals("bb", cache.get(2));
        // state: 4 5 2
    }
}
