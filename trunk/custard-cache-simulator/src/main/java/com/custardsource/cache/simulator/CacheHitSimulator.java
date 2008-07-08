package com.custardsource.cache.simulator;

import java.text.NumberFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.custardsource.cache.policy.CacheManager;
import com.custardsource.cache.policy.CacheManagerListener;

public class CacheHitSimulator {
    private static final Log LOG = LogFactory.getLog(CacheHitSimulator.class);

    public CacheHitSimulator(SimulatorConfiguration configuration) throws Exception {
        this.config = configuration;
    }

    private SimulatorConfiguration config;

    public void testPerformance() throws Exception {
        int iterations = 0;
        long begin = System.currentTimeMillis();
        CountingListener listener = new CountingListener();
    	CacheManager<String> manager = config.getPolicy();
    	manager.addListener(listener);

        for (String fqn : config.getFqnSource()) {
        	manager.visit(fqn);

        	iterations++;

            if (iterations % config.getShowProgressEvery() == 0) {
                System.out.print(".");
            }
        }
        System.out.println();
        long end = System.currentTimeMillis();

        long misses = listener.loads;
        long hits = iterations - misses;
        double hitRatio = (double) hits / (double) iterations;
        NumberFormat format = NumberFormat.getPercentInstance();
        format.setMinimumFractionDigits(3);
        format.setMaximumFractionDigits(3);

        // TODO measure cpu time rather than just wall time
        LOG.info(config.getPolicy() + ": Tests=" + iterations + ", hits/misses=" + hits + "/"
                + misses + " (" + format.format(hitRatio) + "), time=" + (end - begin)
                + ", final # of nodes: " + manager.cacheSize());
    }
    
    private static class CountingListener implements CacheManagerListener<String> {
    	private int loads;

		public void objectReadyForEviction(String item) {
		}

		public void objectLoaded(String item) {
			loads++;
		}
    }
}
