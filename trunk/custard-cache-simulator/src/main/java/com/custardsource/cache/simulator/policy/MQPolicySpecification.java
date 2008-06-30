package com.custardsource.cache.simulator.policy;

import com.custardsource.cache.jboss2.mq.MQPolicy;


public class MQPolicySpecification extends StandardPolicySpecification {
    private final int queueCount;
    private final int lifetime;

    public MQPolicySpecification(int queueCount, int lifetime) {
        super(MQPolicy.class);
        this.queueCount = queueCount;
        this.lifetime = lifetime;
    }

    @Override
    public String getExtraAttributes(int maxNodes) {
        return super.getExtraAttributes(maxNodes) + "<attribute name=\"queueCount\">"
                + queueCount + "</attribute>" + "<attribute name=\"lifetime\">"
                + lifetime + "</attribute>";
    }

    @Override
    public String toString() {
        return super.toString() + " w/ " + queueCount + " queues, " + lifetime + " lifetime";
    }
}