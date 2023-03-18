package com.delix.deliveryou.exception;

public class InternalServerHttpException extends RuntimeException {
    public InternalServerHttpException(String message) {
        super(message);
    }

    public InternalServerHttpException(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalServerHttpException(Throwable cause) {
        super(cause);
    }

    public InternalServerHttpException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public InternalServerHttpException() {
        super();
    }
}
