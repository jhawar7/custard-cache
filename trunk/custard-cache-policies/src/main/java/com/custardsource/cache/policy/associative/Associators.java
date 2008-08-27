package com.custardsource.cache.policy.associative;

public class Associators {
    public static <T> Associator<T> hashCodeAssociator() {
        return new Associator<T>() {
            public int calculateLocation(T item) {
                return Math.abs(item.hashCode());
            }};
        
    }
}
