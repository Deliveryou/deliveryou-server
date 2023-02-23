package com.delix.deliveryou.functionalInterface;

import java.util.function.Supplier;

public interface ExceptionThrowingSupplier<T> {
    T get() throws Exception;
}
