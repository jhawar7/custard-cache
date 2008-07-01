package com.custardsource.cache.policy.twoq;

import com.custardsource.cache.policy.BasicConfiguration;

// TODO there are a couple of options on 2q which should be configurable; even if they're not (yet),
// we still want this a separate class so as not to force users to change a config class later
public class TwoQConfiguration extends BasicConfiguration {
	public TwoQConfiguration() {
		super();
	}

	public TwoQConfiguration(int maxNodes) {
		super(maxNodes);
	}
}