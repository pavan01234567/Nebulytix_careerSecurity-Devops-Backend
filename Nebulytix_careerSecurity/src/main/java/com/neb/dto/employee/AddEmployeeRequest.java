package com.neb.dto.employee;

import java.time.LocalDate;

import lombok.Data;

@Data
public class AddEmployeeRequest {

	private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private String cardNumber;
    private String designation;  
    private String department;   
    private String gender;
    private LocalDate joiningDate;

    private Double salary;
    private int daysPresent;
    private int paidLeaves;
}
