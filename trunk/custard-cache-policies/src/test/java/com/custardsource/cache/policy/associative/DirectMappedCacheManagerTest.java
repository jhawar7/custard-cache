package com.custardsource.cache.policy.associative;

import com.custardsource.cache.policy.BasicCacheStateTest;

public class DirectMappedCacheManagerTest extends BasicCacheStateTest {
	public void testQueueState() {
		DirectMappedCacheManager<Character> manager = new DirectMappedCacheManager<Character>(
				new DirectMappedConfiguration<Character>(3, new Associator<Character>() {
                    public int calculateLocation(Character item) {
                        return item.charValue() - (int) 'A';
                    }}));
		EvictionListener listener = new EvictionListener();
		manager.addListener(listener);
        assertStateAfterVisit(manager, listener, 'A', "[A, null, null]", 1, 0);
        assertStateAfterVisit(manager, listener, 'B', "[A, B, null]", 1, 0);
        assertStateAfterVisit(manager, listener, 'D', "[D, B, null]", 1, 1);
	}
}