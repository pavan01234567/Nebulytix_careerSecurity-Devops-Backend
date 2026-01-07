package com.neb.exception;

public class EmployeeAlreadyLoggedInException extends RuntimeException {
    public EmployeeAlreadyLoggedInException(String message) {
        super(message);
    }
}
