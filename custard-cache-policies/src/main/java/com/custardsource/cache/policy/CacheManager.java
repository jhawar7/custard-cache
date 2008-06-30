package com.custardsource.cache.policy;

public interface CacheManager<T> {
    public void add(T hit);
    public void visit(T hit);
    public void remove(T hit);
    public void addListener(CacheManagerListener<T> listener);
    public void removeListener(CacheManagerListener<T> listener);
    public void clear();
    public int cacheSize();
}