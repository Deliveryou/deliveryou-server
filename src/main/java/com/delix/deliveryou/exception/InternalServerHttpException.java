package com.delix.deliveryou.exception;

public class InternalServerHttpException extends RuntimeException {
    public InternalServerHttpException(String message) {
        super(message);
    }

    public InternalServerHttpException() {
        super();
    }
}
