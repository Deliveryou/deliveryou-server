package com.delix.deliveryou.exception;

public class InsufficientArgumentException extends RuntimeException {
    public InsufficientArgumentException() {
    }

    public InsufficientArgumentException(String message) {
        super(message);
    }

    public InsufficientArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public InsufficientArgumentException(Throwable cause) {
        super(cause);
    }

    public InsufficientArgumentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
