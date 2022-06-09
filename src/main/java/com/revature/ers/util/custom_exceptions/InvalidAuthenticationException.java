package com.revature.ers.util.custom_exceptions;

public class InvalidAuthenticationException extends RuntimeException{

    public InvalidAuthenticationException() {super();}

    public InvalidAuthenticationException(String message) {super(message);}
}
