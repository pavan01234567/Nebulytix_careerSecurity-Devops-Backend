package com.neb.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ResponseMessage<T> {

    private int statusCode;
    private String status;
    private String message;
    private T data; // for single object OR list
    
    public ResponseMessage(int statusCode, String status, String message) {
        this.statusCode = statusCode;
        this.status = status;
        this.message = message;
    }
}
