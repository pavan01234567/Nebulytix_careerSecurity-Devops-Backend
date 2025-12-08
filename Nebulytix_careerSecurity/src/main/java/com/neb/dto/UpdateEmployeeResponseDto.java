package com.neb.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class UpdateEmployeeResponseDto {

	private Long id;
	private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private String cardNumber;

    private String loginRole;   // "hr" or "employee"
    private String jobRole;     // Required if loginRole = "employee"
    private String domain;      // Example: Java, .Net, Python
    private String gender;
    private LocalDate joiningDate;
    private Double salary;
    private int daysPresent;
    private int paidLeaves;
    
    private String bankAccountNumber;
    private String bankName;
    private String pfNumber;
    private String panNumber;
    private String uanNumber;
    private String epsNumber;
    private String esiNumber;
}
