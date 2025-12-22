package com.neb.dto;

import java.time.LocalDate;

import com.neb.entity.LeaveType;

import lombok.Data;

@Data
public class ApplyLeaveRequestDto {
    private Long employeeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private LeaveType leaveType;
    private String reason;
}
