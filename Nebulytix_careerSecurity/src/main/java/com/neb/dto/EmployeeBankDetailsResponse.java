package com.neb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeBankDetailsResponse {

    private Long employeeId;
    private String bankAccountNumber;
    private String ifscCode;
    private String bankName;
    private String pfNumber;
    private String panNumber;
    private String uanNumber;
    private String epsNumber;
    private String esiNumber;
}

