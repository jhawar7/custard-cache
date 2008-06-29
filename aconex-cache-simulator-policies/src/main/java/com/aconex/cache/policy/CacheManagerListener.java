package com.aconex.cache.policy;

public interface CacheManagerListener<T> {
    public void evictObject(T item);
    public void loadObject(T item);
}
