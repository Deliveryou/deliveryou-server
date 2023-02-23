package com.delix.deliveryou.exception;

public class InvalidJsonBodyException extends RuntimeException {
    public InvalidJsonBodyException(String message) {
        super(message);
    }
}
