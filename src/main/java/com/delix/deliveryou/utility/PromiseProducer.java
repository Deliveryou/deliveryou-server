package com.delix.deliveryou.utility;

import com.delix.deliveryou.functionalInterface.ExceptionThrowingSupplier;
import com.delix.deliveryou.functionalInterface.Producer;

import java.util.function.Supplier;

/**
 *
 */
public class PromiseProducer<T> {

    public T produce(ExceptionThrowingSupplier<T> callback, Producer<Exception, T> exceptionProducer) {
        try {
           return callback.get();
        } catch (Exception ex) {
            return exceptionProducer.produce(ex);
        }
    }

}
