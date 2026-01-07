package com.neb.exception;

public class EmployeeNotLoggedInException extends RuntimeException {
    public EmployeeNotLoggedInException(String message) {
        super(message);
    }
}
