package com.neb.dto.employee;

import lombok.Data;
import java.time.LocalDate;

@Data
public class EmployeeProfileDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String designation;
    private String department;
    private String cardNumber;
    private String email;
    private String mobile;
    private String gender;
    private LocalDate joiningDate;
    private Double salary;
    private String profilePictureUrl;
    private String empStatus;
    private boolean userEnabled;
}