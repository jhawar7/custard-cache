package com.custardsource.cache.policy.simple;

import com.custardsource.cache.policy.BasicCacheStateTest;



public class FifoCacheManagerTest extends BasicCacheStateTest {
	public void testQueueState() {
		FifoCacheManager<Character> manager = new FifoCacheManager<Character>(
				new FifoConfiguration(3));
		EvictionListener listener = new EvictionListener();
		manager.addListener(listener);
		assertStateAfterVisit(manager, listener, 'A', "[A]", 1, 0);
		assertStateAfterVisit(manager, listener, 'A', "[A]", 0, 0);
		assertStateAfterVisit(manager, listener, 'B', "[A, B]", 1, 0);
		assertStateAfterVisit(manager, listener, 'C', "[A, B, C]", 1, 0);
		assertStateAfterVisit(manager, listener, 'D', "[B, C, D]", 1, 1);
		assertStateAfterVisit(manager, listener, 'C', "[B, C, D]", 0, 0);
	}
}