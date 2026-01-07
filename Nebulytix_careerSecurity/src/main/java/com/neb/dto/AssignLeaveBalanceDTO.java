package com.neb.dto;

import lombok.Data;
import java.util.Map;

@Data
public class AssignLeaveBalanceDTO {
    private Long employeeId;
    private Integer year;
    private Map<String, Long> leaveAllocation;  
}
