package com.custardsource.cache.policy.simple;

import com.custardsource.cache.policy.CacheManagerListener;

import junit.framework.TestCase;

public class FifoCacheManagerTest extends TestCase {
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

	private void assertStateAfterVisit(FifoCacheManager<Character> manager,
			char visit, String expectedState) {
		manager.visit(visit);
		assertEquals("Cache state does not match expected", expectedState,
				manager.debugString());
	}

	private void assertStateAfterVisit(FifoCacheManager<Character> manager,
			char visit, String expectedState, EvictionListener listener,
			int expectedEvictions) {
		assertStateAfterVisit(manager, visit, expectedState);
		assertEquals("Expected eviction count does not match",
				expectedEvictions, listener.getAndReset());
	}

	public class EvictionListener implements CacheManagerListener<Character> {
		private int evictionCount = 0;

		public void objectReadyForEviction(Character item) {
			evictionCount++;
		}

		public int getAndReset() {
			int result = evictionCount;
			evictionCount = 0;
			return result;
		}

		public void objectLoaded(Character item) {
		}
	}
}