package com.custardsource.cache.policy.associative;

public interface Associator<T> {
    int calculateLocation(T item);
}
