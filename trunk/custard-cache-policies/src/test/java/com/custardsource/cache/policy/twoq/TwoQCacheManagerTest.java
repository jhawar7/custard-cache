package com.custardsource.cache.policy.twoq;

import com.custardsource.cache.policy.BasicCacheStateTest;

public class TwoQCacheManagerTest extends BasicCacheStateTest {
    public void testQueueState() {
        TwoQCacheManager<Character> manager = new TwoQCacheManager<Character>(
                new TwoQConfiguration(4, 0.25f));
        EvictionListener listener = new EvictionListener();
        manager.addListener(listener);
        assertStateAfterVisit(manager, listener, 'A', "am:[], a1In:[A], a1Out:[]", 1, 0);
        assertStateAfterVisit(manager, listener, 'B', "am:[], a1In:[A, B], a1Out:[]", 1, 0);
        assertStateAfterVisit(manager, listener, 'C', "am:[], a1In:[A, B, C], a1Out:[]", 1, 0);
        assertStateAfterVisit(manager, listener, 'D', "am:[], a1In:[A, B, C, D], a1Out:[]", 1, 0);
        assertStateAfterVisit(manager, listener, 'E', "am:[], a1In:[B, C, D, E], a1Out:[A]", 1, 1);
        assertStateAfterVisit(manager, listener, 'A', "am:[A], a1In:[C, D, E], a1Out:[B]", 1, 1);
        assertStateAfterVisit(manager, listener, 'F', "am:[A], a1In:[D, E, F], a1Out:[B, C]", 1, 1);
        assertStateAfterVisit(manager, listener, 'G', "am:[A], a1In:[E, F, G], a1Out:[C, D]", 1, 1);
        // Keeps moving to am until we get back down to preferred size of a1in
        assertStateAfterVisit(manager, listener, 'C', "am:[A, C], a1In:[F, G], a1Out:[D, E]", 1, 1);
        assertStateAfterVisit(manager, listener, 'D', "am:[A, C, D], a1In:[G], a1Out:[E, F]", 1, 1);
        // Now a1in is the right size, moves to am should mean dropping from am altogether
        assertStateAfterVisit(manager, listener, 'E', "am:[C, D, E], a1In:[G], a1Out:[F]", 1, 1);
    }
}
