package com.aconex.cache.simulator;

import org.jboss.cache.Fqn;

import com.aconex.cache.simulator.policy.PolicySpecification;

class SimulatorConfiguration {
    private final PolicySpecification policy;
    private final Iterable<Fqn> fqnSource;
    private final int maxNodes;
    private final int reapEvery;
    private final int minNodes;
    private final int showProgressEvery;
    
    public SimulatorConfiguration(PolicySpecification policy, Iterable<Fqn> fqnSource, int maxNodes,
            int minNodes, int reapEvery, int showProgressEvery) {
        this.policy = policy;
        this.fqnSource = fqnSource;
        this.maxNodes = maxNodes;
        this.minNodes = minNodes;
        this.reapEvery = reapEvery;
        this.showProgressEvery = showProgressEvery;
    }

    PolicySpecification getPolicy() {
        return policy;
    }

    Iterable<Fqn> getFqnSource() {
        return fqnSource;
    }

    int getMaxNodes() {
        return maxNodes;
    }

    int getReapEvery() {
        return reapEvery;
    }

    int getMinNodes() {
        return minNodes;
    }

    int getShowProgressEvery() {
        return showProgressEvery;
    }
}