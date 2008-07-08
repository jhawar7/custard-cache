package com.custardsource.cache.policy.simple;

import com.custardsource.cache.policy.BasicCacheStateTest;



public class LruCacheManagerTest extends BasicCacheStateTest {
	public void testQueueState() {
		LruCacheManager<Character> manager = new LruCacheManager<Character>(
				new LruConfiguration(3));
		EvictionListener listener = new EvictionListener();
		manager.addListener(listener);
		assertStateAfterVisit(manager, listener, 'A', "[A]", 1, 0);
		assertStateAfterVisit(manager, listener, 'A', "[A]", 0, 0);
		assertStateAfterVisit(manager, listener, 'B', "[A, B]", 1, 0);
		assertStateAfterVisit(manager, listener, 'A', "[B, A]", 0, 0);
		assertStateAfterVisit(manager, listener, 'C', "[B, A, C]", 1, 0);
		assertStateAfterVisit(manager, listener, 'D', "[A, C, D]", 1, 1);
		assertStateAfterVisit(manager, listener, 'C', "[A, D, C]", 0, 0);
	}
}