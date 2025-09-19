package com.viooh.supermarket.exceptions;
public class RulesException extends RuntimeException {
    public RulesException(String message) {
        super(message);
    }
    public RulesException(String message, Throwable cause) {
        super(message, cause);
    }
}


