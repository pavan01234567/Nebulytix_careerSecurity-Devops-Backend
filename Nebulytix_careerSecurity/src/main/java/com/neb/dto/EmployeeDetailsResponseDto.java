package com.neb.dto;

import java.time.LocalDate;

import lombok.Data;


@Data
public class EmployeeDetailsResponseDto {

	private Long id;
	private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private String cardNumber;
    private String jobRole;     
    private String domain;
    private String gender;
    private LocalDate joiningDate;
    private Double salary;
    private int daysPresent;
    private int paidLeaves;
    private String loginRole;
    private String profilePictureUrl;
    
    private String bankAccountNumber;
    private String ifscCode;
    private String bankName;
    private String pfNumber;
    private String panNumber;
    private String uanNumber;
    private String epsNumber;
    private String esiNumber;
}
