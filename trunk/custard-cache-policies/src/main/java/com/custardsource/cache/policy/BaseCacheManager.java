package com.custardsource.cache.policy;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseCacheManager<T, U extends BasicConfiguration> implements CacheManager<T> {
    public List<CacheManagerListener<T>> listeners = new ArrayList<CacheManagerListener<T>>();
    private U config;
    
    public void addListener(CacheManagerListener<T> listener) {
        listeners.add(listener);
    }

    public void removeListener(CacheManagerListener<T> listener) {
        listeners.remove(listener);
    }
    
    protected void evict(T object) {
        for (CacheManagerListener<T> listener : listeners) {
            listener.objectReadyForEviction(object);
        }
    }

    protected void load(T object) {
        for (CacheManagerListener<T> listener : listeners) {
            listener.objectLoaded(object);
        }
    }
    
    protected final void setConfig(U config) {
        this.config = config;
    }

    protected final U getConfig() {
        return this.config;
    }
        
    protected abstract String debugString();
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + " {" + getConfig() + "}";
    }
}
