package com.neb.dto;

import com.neb.util.EmployeeLeaveType;

import lombok.Data;

@Data
public class EmployeeLeaveBalanceDTO {
    private Long id;
    private Long employeeId;
    private EmployeeLeaveType leaveType;
    private Integer currentYear;
    private Long totalAllowed;
    private Long used;
    private Long remaining;
}
