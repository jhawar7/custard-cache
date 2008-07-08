package com.custardsource.cache.policy.simple;

import com.custardsource.cache.policy.BasicCacheStateTest;



public class FifoCacheManagerTest extends BasicCacheStateTest {
	public void testQueueState() {
		FifoCacheManager<Character> manager = new FifoCacheManager<Character>(
				new FifoConfiguration(3));
		EvictionListener listener = new EvictionListener();
		manager.addListener(listener);
		assertStateAfterVisit(manager, 'A', "[A]");
		assertStateAfterVisit(manager, 'B', "[A, B]");
		assertStateAfterVisit(manager, 'C', "[A, B, C]");
		assertStateAfterVisit(manager, 'D', "[B, C, D]", listener, 1);
		assertStateAfterVisit(manager, 'C', "[B, C, D]");
	}
}