package com.neb.dto.salary;

import java.time.LocalDate;

import lombok.Data;
@Data
public class SalaryResponseDto {

    private Long id;
    private Long employeeId;
    private Double basicSalary;
    private Double hra;
    private Double allowance;
    private Double deductions;
    private Double netSalary;
    private LocalDate effectiveFrom;
    private boolean active;

}
