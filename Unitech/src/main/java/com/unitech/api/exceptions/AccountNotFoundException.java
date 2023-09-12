package com.unitech.api.exceptions;

public class AccountNotFoundException extends RuntimeException {
    private static final long serialVerisionUID = 1;

    public AccountNotFoundException(String message) {
        super(message);
    }
}
