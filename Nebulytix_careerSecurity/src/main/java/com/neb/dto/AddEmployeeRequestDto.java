
package com.neb.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AddEmployeeRequestDto {

    private String firstName;
    private String lastName;
   
    private String mobile;
    private String cardNumber;
    private String designation;  // e.g., Senior Java Developer, HR Executive//new
    private String department;   // e.g., HR, Java, Finance, QA//new
    
    private String gender;
    private LocalDate joiningDate;
    private Double salary;
    private int daysPresent;
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
