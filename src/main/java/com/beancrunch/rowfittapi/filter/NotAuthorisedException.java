package com.beancrunch.rowfittapi.filter;

public class NotAuthorisedException extends RuntimeException {

    public NotAuthorisedException(String errorMessage) {
        super(errorMessage);
    }

    public NotAuthorisedException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }
}
