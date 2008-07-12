package com.custardsource.cache.policy.simple;

import com.custardsource.cache.policy.BasicCacheStateTest;

public class LfuCacheManagerTest extends BasicCacheStateTest {
    public void testQueueState() {
        LfuCacheManager<Character> manager = new LfuCacheManager<Character>(new LfuConfiguration(3));
        EvictionListener listener = new EvictionListener();
        manager.addListener(listener);
        assertStateAfterVisit(manager, listener, 'A', "[1=[A]]", 1, 0);
        assertStateAfterVisit(manager, listener, 'B', "[1=[A, B]]", 1, 0);
        assertStateAfterVisit(manager, listener, 'C', "[1=[A, B, C]]", 1, 0);
        // Evicts oldest
        assertStateAfterVisit(manager, listener, 'D', "[1=[B, C, D]]", 1, 1);
        assertStateAfterVisit(manager, listener, 'B', "[1=[C, D], 2=[B]]", 0, 0);
        assertStateAfterVisit(manager, listener, 'C', "[1=[D], 2=[B, C]]", 0, 0);
        // Removes empty visit counts
        assertStateAfterVisit(manager, listener, 'D', "[2=[B, C, D]]", 0, 0);
        assertStateAfterVisit(manager, listener, 'B', "[2=[C, D], 3=[B]]", 0, 0);
        assertStateAfterVisit(manager, listener, 'E', "[1=[E], 2=[D], 3=[B]]", 1, 1);
    }
}