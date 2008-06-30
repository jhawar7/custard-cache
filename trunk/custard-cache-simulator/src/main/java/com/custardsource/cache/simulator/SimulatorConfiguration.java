package com.custardsource.cache.simulator;

import com.custardsource.cache.policy.CacheManager;

class SimulatorConfiguration {
	private final CacheManager<String> policy;
	private final Iterable<String> fqnSource;
	private final int showProgressEvery;

	public SimulatorConfiguration(CacheManager<String> policy,
			Iterable<String> fqnSource, int showProgressEvery) {
		this.policy = policy;
		this.fqnSource = fqnSource;
		this.showProgressEvery = showProgressEvery;
	}

	CacheManager<String> getPolicy() {
		return policy;
	}

	Iterable<String> getFqnSource() {
		return fqnSource;
	}

	int getShowProgressEvery() {
		return showProgressEvery;
	}
}