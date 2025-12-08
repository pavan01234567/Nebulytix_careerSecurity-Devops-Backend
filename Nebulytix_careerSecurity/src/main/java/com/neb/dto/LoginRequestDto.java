package com.neb.dto;

import lombok.Data;


@Data
public class LoginRequestDto {
    private String email;
    private String password;
    private String loginRole; // admin/hr/employee
}
