package com.custardsource.cache.policy.replacement;

import com.custardsource.cache.policy.BasicCacheStateTest;

public class AdaptiveReplacementCacheManagerTest extends BasicCacheStateTest {
	public void testQueueState() {
		AdaptiveReplacementCacheManager<Character> manager = new AdaptiveReplacementCacheManager<Character>(
				new AdaptiveReplacementConfiguration(5));
		EvictionListener listener = new EvictionListener();
		manager.addListener(listener);
		assertStateAfterVisit(manager, listener, 'A', "t1:[A], t2:[], b1:[], b2:[] t0", 1, 0);
		assertStateAfterVisit(manager, listener, 'B', "t1:[A, B], t2:[], b1:[], b2:[] t0", 1, 0);
		assertStateAfterVisit(manager, listener, 'C', "t1:[A, B, C], t2:[], b1:[], b2:[] t0", 1, 0);
		assertStateAfterVisit(manager, listener, 'D', "t1:[A, B, C, D], t2:[], b1:[], b2:[] t0", 1, 0);
		assertStateAfterVisit(manager, listener, 'E', "t1:[A, B, C, D, E], t2:[], b1:[], b2:[] t0", 1, 0);
        assertStateAfterVisit(manager, listener, 'F', "t1:[B, C, D, E, F], t2:[], b1:[], b2:[] t0", 1, 1);
        // Hit moves to t2
        assertStateAfterVisit(manager, listener, 'E', "t1:[B, C, D, F], t2:[E], b1:[], b2:[] t0", 0, 0);
        assertStateAfterVisit(manager, listener, 'F', "t1:[B, C, D], t2:[E, F], b1:[], b2:[] t0", 0, 0);
        // Hit in t2 moves to head
        assertStateAfterVisit(manager, listener, 'E', "t1:[B, C, D], t2:[F, E], b1:[], b2:[] t0", 0, 0);
        // Load when t1 size is > target evicts t1 -> b1
        assertStateAfterVisit(manager, listener, 'G', "t1:[C, D, G], t2:[F, E], b1:[B], b2:[] t0", 1, 1);
        // Hit in b1 moves to t2 and pushes from t1, increases target t1 size
        assertStateAfterVisit(manager, listener, 'B', "t1:[D, G], t2:[F, E, B], b1:[C], b2:[] t1", 1, 1);
        assertStateAfterVisit(manager, listener, 'D', "t1:[G], t2:[F, E, B, D], b1:[C], b2:[] t1", 0, 0);
        // Load when t1 size is <= target evicts t2 -> b2
        assertStateAfterVisit(manager, listener, 'H', "t1:[G, H], t2:[E, B, D], b1:[C], b2:[F] t1", 1, 1);
        // b2 hit when size is = target evicts t1 -> b1, decreases target t1 size
        assertStateAfterVisit(manager, listener, 'F', "t1:[H], t2:[E, B, D, F], b1:[C, G], b2:[] t0", 1, 1);
	}
}
