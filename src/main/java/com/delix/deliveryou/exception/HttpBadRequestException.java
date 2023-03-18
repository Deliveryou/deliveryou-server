package com.delix.deliveryou.exception;

public class HttpBadRequestException extends RuntimeException{
    public HttpBadRequestException() {
    }

    public HttpBadRequestException(String message) {
        super(message);
    }

    public HttpBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpBadRequestException(Throwable cause) {
        super(cause);
    }

    public HttpBadRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
