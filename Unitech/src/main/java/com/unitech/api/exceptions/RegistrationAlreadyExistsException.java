package com.unitech.api.exceptions;

public class RegistrationAlreadyExistsException extends RuntimeException {
    private static final long serialVerisionUID = 2;

    public RegistrationAlreadyExistsException(String message) {
        super(message);
    }
}
