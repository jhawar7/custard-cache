package com.custardsource.cache.jboss2.mq;

import org.jboss.cache.ConfigureException;
import org.jboss.cache.eviction.EvictionConfiguration;
import org.jboss.cache.xml.XmlHelper;
import org.w3c.dom.Element;

import com.custardsource.cache.jboss2.MultipleQueueConfigurator;
import com.custardsource.cache.policy.mq.MQConfiguration;


public class MQConfigurator extends MultipleQueueConfigurator<MQConfiguration> {
    public static final String QUEUE_COUNT = "queueCount";
    public static final String LIFETIME = "lifetime";

    @Override
    public void parseXMLConfig(Element element) throws ConfigureException {
        super.parseXMLConfig(element);
        String queueCount = XmlHelper.getAttr(element, QUEUE_COUNT,
                EvictionConfiguration.ATTR, EvictionConfiguration.NAME);
        if (queueCount == null || queueCount.equals("")) {
            throw new ConfigureException(
            "MQConfiguration requires queueCount attribute");
        }
        config.setQueueCount(Integer.parseInt(queueCount));

        String lifetime = XmlHelper.getAttr(element, LIFETIME,
                EvictionConfiguration.ATTR, EvictionConfiguration.NAME);
        if (lifetime == null || lifetime.equals("")) {
            throw new ConfigureException(
            "MQConfiguration requires lifetime attribute");
        }
        config.setLifetime(Integer.parseInt(lifetime));
}

    @Override
    public final MQConfiguration createConfig() {
        return new MQConfiguration();
    }
}