package com.custardsource.cache.policy.associative;

import com.custardsource.cache.policy.BasicConfiguration;

public class DirectMappedConfiguration<T> extends BasicConfiguration {
    private Associator<T> associator = Associators.hashCodeAssociator();

    public DirectMappedConfiguration() {
        super();
    }

    public DirectMappedConfiguration(int maxNodes) {
        super(maxNodes);
    }
    
    public DirectMappedConfiguration(int maxNodes, Associator<T> associator) {
        super(maxNodes);
        this.associator = associator;
    }

    public Associator<T> getAssociator() {
        return associator;
    }
}
