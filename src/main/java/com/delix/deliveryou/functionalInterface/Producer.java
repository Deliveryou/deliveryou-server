package com.delix.deliveryou.functionalInterface;

public interface Producer<T, V> {
    V produce(T value);
}
