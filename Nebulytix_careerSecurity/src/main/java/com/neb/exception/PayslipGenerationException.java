package com.neb.exception;

@SuppressWarnings("serial")
public class PayslipGenerationException extends RuntimeException {

    public PayslipGenerationException(String message) {
        super(message);
    }

    public PayslipGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}

