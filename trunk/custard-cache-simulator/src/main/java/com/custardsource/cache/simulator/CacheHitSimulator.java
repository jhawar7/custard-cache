package com.custardsource.cache.simulator;

import java.io.StringReader;
import java.text.NumberFormat;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.cache.Fqn;
import org.jboss.cache.TreeCache;
import org.jboss.cache.eviction.Region;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import com.custardsource.cache.simulator.policy.PolicySpecification;

public class CacheHitSimulator {
    private static final Log LOG = LogFactory.getLog(CacheHitSimulator.class);

    public CacheHitSimulator(SimulatorConfiguration configuration) throws Exception {
        this.config = configuration;
        initCache(config.getPolicy(), config.getMaxNodes(), config.getMinNodes());
    }

    private TreeCache cache;
    private DummyCacheLoader loader;
    private SimulatorConfiguration config;

    protected void nuke() throws Exception {
        cache.stopService();
        cache.destroyService();
    }

    @SuppressWarnings("deprecation")
    protected void initCache(PolicySpecification policySpecification, int maxNodes, int minNodes)
            throws Exception {
        cache = new TreeCache();
        loader = new DummyCacheLoader();
        cache.setCacheLoader(loader);
        cache.setCacheMode(TreeCache.LOCAL);
        cache.setEvictionPolicyClass(policySpecification.getPolicy());
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        Element e = factory.newDocumentBuilder().parse(
                new InputSource(new StringReader("<config>"
                        + "<attribute name=\"wakeUpIntervalSeconds\">10000</attribute>"
                        + "<region name=\"/_default_\">" + "<attribute name=\"maxNodes\">"
                        + maxNodes + "</attribute>" + policySpecification.getExtraAttributes(maxNodes)
                        + "<attribute name=\"timeToIdleSeconds\">0</attribute>"
                        + "</region></config>"))).getDocumentElement();
        cache.setEvictionPolicyConfig(e);
        cache.createService();
        cache.startService();
    }

    public void testPerformance() throws Exception {
        int iterations = 0;
        long begin = System.currentTimeMillis();
        for (Fqn fqn : config.getFqnSource()) {
            cache.get(fqn, "value");

            iterations++;

            // Force eviction
            if (iterations % config.getReapEvery() == 0) {
                Region r = cache.getEvictionRegionManager().getRegion("/");
                r.getEvictionPolicy().getEvictionAlgorithm().process(r);
            }
            if (iterations % config.getShowProgressEvery() == 0) {
                System.out.print(".");
            }
        }
        System.out.println();
        long end = System.currentTimeMillis();

        long misses = loader.getLoadCount();
        long hits = iterations - misses;
        double hitRatio = (double) hits / (double) iterations;
        NumberFormat format = NumberFormat.getPercentInstance();
        format.setMinimumFractionDigits(3);
        format.setMaximumFractionDigits(3);

        // TODO measure cpu time rather than just wall time
        LOG.info(config.getPolicy() + ": Tests=" + iterations + ", hits/misses=" + hits + "/"
                + misses + " (" + format.format(hitRatio) + "), time=" + (end - begin)
                + ", final # of nodes: " + cache.getNumberOfNodes());
    }
}
