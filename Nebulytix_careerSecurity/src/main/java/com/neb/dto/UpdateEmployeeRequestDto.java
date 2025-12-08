package com.neb.dto;


import lombok.Data;

@Data
public class UpdateEmployeeRequestDto {

	private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private String cardNumber;
    private String jobRole;     
    private String domain;      
    private String gender;
    private Double salary;
    private int paidLeaves;
    
    private String bankAccountNumber;
    private String ifscCode;
    private String bankName;
    private String pfNumber;
    private String panNumber;
    private String uanNumber;
    private String epsNumber;
    private String esiNumber;
}
