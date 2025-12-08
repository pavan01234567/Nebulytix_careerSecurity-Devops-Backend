package com.neb.exception;



public class HrNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public HrNotFoundException(String message) {
        super(message);
    }
}
