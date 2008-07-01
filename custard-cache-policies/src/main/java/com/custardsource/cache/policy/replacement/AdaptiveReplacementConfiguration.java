package com.custardsource.cache.policy.replacement;

import com.custardsource.cache.policy.BasicConfiguration;


public class AdaptiveReplacementConfiguration extends BasicConfiguration {
	// This class has no implementation; however we want to enforce people using this rather than
	// the base class in case we add some more properties later

	public AdaptiveReplacementConfiguration() {
		super();
	}

	public AdaptiveReplacementConfiguration(int maxNodes) {
		super(maxNodes);
	}
}