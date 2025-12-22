package com.neb.service;

import java.time.LocalDate;
import java.util.Map;

import com.neb.entity.Employee;
import com.neb.entity.Leave;

public interface LeaveService {

    Leave applyLeave(Employee employee, LocalDate from, LocalDate to, Leave.LeaveType type);

    Leave approveLeave(Long leaveId);

    Leave rejectLeave(Long leaveId);

    Map<String, Integer> getLeaveBalance(Employee employee);
}
