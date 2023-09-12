package com.unitech.api.exceptions;

public class UnacceptableAmountException extends RuntimeException {
    private static final long serialVerisionUID = 5;

    public UnacceptableAmountException(String message) {
        super(message);
    }
}
