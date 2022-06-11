package com.revature.ers.util.custom_exceptions;

public class NotAuthorizedException extends RuntimeException {
    public NotAuthorizedException() {
        super();
    }

    public NotAuthorizedException(String message) {
        super(message);
    }
}
