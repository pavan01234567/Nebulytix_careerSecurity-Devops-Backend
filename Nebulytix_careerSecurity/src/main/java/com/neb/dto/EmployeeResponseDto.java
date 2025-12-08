package com.neb.dto;


import java.time.LocalDate;
import lombok.Data;

@Data
public class EmployeeResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private String cardNumber;//newly added
    private String jobRole;
    private String domain;
    private String loginRole;
    private String gender;
    private LocalDate joiningDate;
    private Double salary;
    private int daysPresent;
    private int paidLeaves;
    
}
