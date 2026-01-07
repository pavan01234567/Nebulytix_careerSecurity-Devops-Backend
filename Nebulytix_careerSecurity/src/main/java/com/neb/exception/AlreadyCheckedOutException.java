package com.neb.exception;

public class AlreadyCheckedOutException extends RuntimeException {
    public AlreadyCheckedOutException(String message) {
        super(message);
    }
}
