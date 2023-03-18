package com.delix.deliveryou.exception;

public class InvalidJsonBodyException extends RuntimeException {
    public InvalidJsonBodyException(String message) {
        super(message);
    }

    public InvalidJsonBodyException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidJsonBodyException(Throwable cause) {
        super(cause);
    }

    public InvalidJsonBodyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public InvalidJsonBodyException() {
    }
}
