package com.custardsource.cache.policy.replacement;

import com.custardsource.cache.policy.BasicCacheStateTest;

public class FixedReplacementCacheManagerTest extends BasicCacheStateTest {
	public void testQueueState() {
		FixedReplacementCacheManager<Character> manager = new FixedReplacementCacheManager<Character>(
				new FixedReplacementConfiguration(5, 2));
		EvictionListener listener = new EvictionListener();
		manager.addListener(listener);
		assertStateAfterVisit(manager, listener, 'A', "t1:[A], t2:[]", 1, 0);
		assertStateAfterVisit(manager, listener, 'B', "t1:[A, B], t2:[]", 1, 0);
		assertStateAfterVisit(manager, listener, 'C', "t1:[A, B, C], t2:[]", 1, 0);
		assertStateAfterVisit(manager, listener, 'D', "t1:[A, B, C, D], t2:[]", 1, 0);
		assertStateAfterVisit(manager, listener, 'E', "t1:[A, B, C, D, E], t2:[]", 1, 0);
		assertStateAfterVisit(manager, listener, 'F', "t1:[B, C, D, E, F], t2:[]", 1, 1);
		assertStateAfterVisit(manager, listener, 'B', "t1:[C, D, E, F], t2:[B]", 0, 0);
		assertStateAfterVisit(manager, listener, 'C', "t1:[D, E, F], t2:[B, C]", 0, 0);
		assertStateAfterVisit(manager, listener, 'B', "t1:[D, E, F], t2:[C, B]", 0, 0);
		assertStateAfterVisit(manager, listener, 'D', "t1:[E, F], t2:[C, B, D]", 0, 0);
		assertStateAfterVisit(manager, listener, 'E', "t1:[F], t2:[C, B, D, E]", 0, 0);
		assertStateAfterVisit(manager, listener, 'G', "t1:[F, G], t2:[B, D, E]", 1, 1);
		assertStateAfterVisit(manager, listener, 'H', "t1:[G, H], t2:[B, D, E]", 1, 1);
	}
}
