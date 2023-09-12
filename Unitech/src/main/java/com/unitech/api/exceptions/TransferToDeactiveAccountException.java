package com.unitech.api.exceptions;

public class TransferToDeactiveAccountException extends RuntimeException {
    private static final long serialVerisionUID = 4;

    public TransferToDeactiveAccountException(String message) {
        super(message);
    }
}
