package com.custardsource.cache.policy.mq;

import com.custardsource.cache.policy.BasicCacheStateTest;

public class MQCacheManagerTest extends BasicCacheStateTest {
    public void testQueueState() {
        MQCacheManager<Character> manager = new MQCacheManager<Character>(
                new MQConfiguration(6, 3, 2));
        EvictionListener listener = new EvictionListener();
        manager.addListener(listener);
        assertStateAfterVisit(manager, listener, 'A', "Q0:[A], Q1:[], Q2:[], qOut:[]", 1, 0);
        assertStateAfterVisit(manager, listener, 'A', "Q0:[], Q1:[A], Q2:[], qOut:[]", 0, 0);
        assertStateAfterVisit(manager, listener, 'A', "Q0:[], Q1:[A], Q2:[], qOut:[]", 0, 0);
        assertStateAfterVisit(manager, listener, 'A', "Q0:[], Q1:[], Q2:[A], qOut:[]", 0, 0);
        assertStateAfterVisit(manager, listener, 'B', "Q0:[B], Q1:[], Q2:[A], qOut:[]", 1, 0);
        // 2 without a visit after promotion means A should drop down
        assertStateAfterVisit(manager, listener, 'C', "Q0:[B, C], Q1:[A], Q2:[], qOut:[]", 1, 0);
        assertStateAfterVisit(manager, listener, 'D', "Q0:[B, C, D], Q1:[A], Q2:[], qOut:[]", 1, 0);
        assertStateAfterVisit(manager, listener, 'E', "Q0:[B, C, D, E], Q1:[A], Q2:[], qOut:[]", 1, 0);
        // 3 without a visit after demotion means A should drop down
        assertStateAfterVisit(manager, listener, 'E', "Q0:[B, C, D, A], Q1:[E], Q2:[], qOut:[]", 0, 0);
        assertStateAfterVisit(manager, listener, 'F', "Q0:[B, C, D, A, F], Q1:[E], Q2:[], qOut:[]", 1, 0);
        // Keep E hit to simplify next case
        assertStateAfterVisit(manager, listener, 'E', "Q0:[B, C, D, A, F], Q1:[E], Q2:[], qOut:[]", 0, 0);
        // Hit when full evicts to qOut 
        assertStateAfterVisit(manager, listener, 'G', "Q0:[C, D, A, F, G], Q1:[E], Q2:[], qOut:[B]", 1, 1);
        // Keep E hit again 
        assertStateAfterVisit(manager, listener, 'E', "Q0:[C, D, A, F, G], Q1:[], Q2:[E], qOut:[B]", 0, 0);
        // Hit in qOut counts as a load and keeps reference count 
        assertStateAfterVisit(manager, listener, 'B', "Q0:[D, A, F, G], Q1:[B], Q2:[E], qOut:[C]", 1, 1);
    }
}
