package com.neb.dto;

import lombok.Data;

@Data
public class EmployeeBankDetailsRequest {
    private String bankAccountNumber;
    private String ifscCode;
    private String bankName;
    private String pfNumber;
    private String panNumber;
    private String uanNumber;
    private String epsNumber;
    private String esiNumber;
}
