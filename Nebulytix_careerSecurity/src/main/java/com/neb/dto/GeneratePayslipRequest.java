package com.neb.dto;

import lombok.Data;
@Data
public class GeneratePayslipRequest {
    private Long employeeId;
    private String monthYear;
}
