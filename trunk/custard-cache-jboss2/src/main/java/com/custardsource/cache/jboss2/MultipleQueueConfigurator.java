package com.custardsource.cache.jboss2;

import org.jboss.cache.ConfigureException;
import org.jboss.cache.eviction.EvictionConfiguration;
import org.jboss.cache.xml.XmlHelper;
import org.w3c.dom.Element;

import com.custardsource.cache.policy.BasicConfiguration;

public abstract class MultipleQueueConfigurator<T extends BasicConfiguration> implements EvictionConfiguration {
    protected final T config;
    
    public MultipleQueueConfigurator() {
        config = createConfig();
    }
    
    public void parseXMLConfig(Element element) throws ConfigureException {
        String name = element.getAttribute(NAME);

        if (name == null || name.equals("")) {
            throw new ConfigureException("Name is required for the eviction region");
        }

        String maxNodes = XmlHelper.getAttr(element, MAX_NODES, EvictionConfiguration.ATTR,
                EvictionConfiguration.NAME);
        if (maxNodes != null && !maxNodes.equals("")) {
            config.setMaxNodes(Integer.parseInt(maxNodes));
        } else {
            throw new ConfigureException(
                    this.getClass().getSimpleName() + " requires maxNodes attribute");
        }
    }
    
    protected abstract T createConfig();

    public final T getConfig() {
        return config;
    }
}
