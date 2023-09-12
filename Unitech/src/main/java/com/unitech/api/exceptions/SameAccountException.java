package com.unitech.api.exceptions;

public class SameAccountException extends RuntimeException {
    private static final long serialVerisionUID = 3;

    public SameAccountException(String message) {
        super(message);
    }
}
