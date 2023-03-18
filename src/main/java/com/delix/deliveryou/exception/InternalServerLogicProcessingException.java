package com.delix.deliveryou.exception;

public class InternalServerLogicProcessingException extends RuntimeException{
    public InternalServerLogicProcessingException() {
    }

    public InternalServerLogicProcessingException(String message) {
        super(message);
    }

    public InternalServerLogicProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalServerLogicProcessingException(Throwable cause) {
        super(cause);
    }

    public InternalServerLogicProcessingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
