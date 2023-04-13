package com.delix.deliveryou.exception;

public class LogicViolationException extends RuntimeException {
    public LogicViolationException() {
    }

    public LogicViolationException(String message) {
        super(message);
    }

    public LogicViolationException(String message, Throwable cause) {
        super(message, cause);
    }

    public LogicViolationException(Throwable cause) {
        super(cause);
    }

    public LogicViolationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
