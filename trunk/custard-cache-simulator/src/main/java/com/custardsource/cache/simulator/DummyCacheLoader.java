package com.custardsource.cache.simulator;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.cache.Fqn;
import org.jboss.cache.TreeCache;
import org.jboss.cache.loader.CacheLoader;

// We don't control the parent class, so we suppress warnings here.
@SuppressWarnings("unchecked")
public class DummyCacheLoader implements CacheLoader {
    AtomicInteger loads = new AtomicInteger();

    public int getLoadCount() {
        return loads.get();
    }

    public boolean exists(Fqn name) throws Exception {
        return true;
    }

    public Map get(Fqn name) throws Exception {
        loads.incrementAndGet();
        return Collections.singletonMap("value", name.toString());
    }

    public void prepare(Object tx, List modifications, boolean one_phase) throws Exception {
    }

    public void put(List modifications) throws Exception {
    }

    public void put(Fqn name, Map attributes) throws Exception {
    }

    public void setCache(TreeCache c) {
    }

    public void create() throws Exception {
    }

    public void destroy() {
    }

    public void start() throws Exception {
    }

    public void stop() {
    }

    public void commit(Object tx) throws Exception {
        throw new UnsupportedOperationException();
    }

    public Set getChildrenNames(Fqn fqn) throws Exception {
        throw new UnsupportedOperationException();
    }

    public byte[] loadEntireState() throws Exception {
        throw new UnsupportedOperationException();
    }

    public Object put(Fqn name, Object key, Object value) throws Exception {
        throw new UnsupportedOperationException();
    }

    public void remove(Fqn name) throws Exception {
        throw new UnsupportedOperationException();
    }

    public Object remove(Fqn name, Object key) throws Exception {
        throw new UnsupportedOperationException();
    }

    public void removeData(Fqn name) throws Exception {
        throw new UnsupportedOperationException();
    }

    public void rollback(Object tx) {
        throw new UnsupportedOperationException();
    }

    public void setConfig(Properties properties) {
        throw new UnsupportedOperationException();
    }

    public void storeEntireState(byte[] state) throws Exception {
        throw new UnsupportedOperationException();
    }
}