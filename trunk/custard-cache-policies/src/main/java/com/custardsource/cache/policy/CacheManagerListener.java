package com.custardsource.cache.policy;

public interface CacheManagerListener<T> {
    public void objectReadyForEviction(T item);
    public void objectLoaded(T item);
}
