package com.custardsource.cache.policy;

import junit.framework.TestCase;

import com.custardsource.cache.policy.CacheManagerListener;

public abstract class BasicCacheStateTest extends TestCase {
	protected void assertStateAfterVisit(BaseCacheManager<Character> manager, char visit,
			String expectedState) {
				manager.visit(visit);
				assertEquals("Cache state does not match expected", expectedState,
						manager.debugString());
			}

	protected void assertStateAfterVisit(BaseCacheManager<Character> manager, char visit,
			String expectedState, EvictionListener listener, int expectedEvictions) {
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