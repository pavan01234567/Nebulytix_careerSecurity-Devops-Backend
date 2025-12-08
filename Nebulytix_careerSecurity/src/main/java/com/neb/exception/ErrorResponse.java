package com.neb.exception;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ErrorResponse {

	private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    
    public ErrorResponse(int statusCode, String errorMessage, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = statusCode;
        this.error = errorMessage;
        this.message = message;
        this.path = path;
    }
}
