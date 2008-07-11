package com.custardsource.cache.policy;

import junit.framework.TestCase;

import com.custardsource.cache.policy.CacheManagerListener;

public abstract class BasicCacheStateTest extends TestCase {
	protected void assertStateAfterVisit(BaseCacheManager<Character, ?> manager,
			EvictionListener listener, char visit, String expectedState,
			int expectedMisses, int expectedEvictions) {
		manager.visit(visit);
		assertEquals("Cache state does not match expected", expectedState,
				manager.debugString());
		assertEquals("Expected miss count does not match",
				expectedMisses, listener.getAndResetMissCount());
		assertEquals("Expected eviction count does not match",
				expectedEvictions, listener.getAndResetEvictionCount());
	}

	public class EvictionListener implements CacheManagerListener<Character> {
		private int evictionCount = 0;
		private int missCount = 0;

		public void objectReadyForEviction(Character item) {
			evictionCount++;
		}

		public int getAndResetEvictionCount() {
			int result = evictionCount;
			evictionCount = 0;
			return result;
		}

		public void objectLoaded(Character item) {
			missCount++;
		}

		public int getAndResetMissCount() {
			int result = missCount;
			missCount = 0;
			return result;
		}
	}
}