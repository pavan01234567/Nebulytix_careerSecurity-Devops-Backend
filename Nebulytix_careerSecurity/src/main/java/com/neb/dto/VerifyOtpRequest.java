package com.neb.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VerifyOtpRequest {
    private String email;
    private String otp;
}
