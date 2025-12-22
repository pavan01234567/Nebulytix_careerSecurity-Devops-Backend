package com.neb.service.impl;



import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neb.entity.Attendance;
import com.neb.entity.Employee;
import com.neb.entity.Leave;
import com.neb.exception.BusinessException;
import com.neb.exception.ResourceNotFoundException;
import com.neb.repo.LeaveRepository;
import com.neb.service.AttendanceService;
import com.neb.service.LeaveService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRepository leaveRepository;
    private final AttendanceService attendanceService;

    @Override
    public Leave applyLeave(Employee employee, LocalDate from, LocalDate to, Leave.LeaveType type) {

        long days = from.datesUntil(to.plusDays(1)).count();

        if (type == Leave.LeaveType.SICK &&
            leaveRepository.findByEmployeeAndTypeAndStatus(employee, type, Leave.LeaveStatus.APPROVED).size() + days > 12)
            throw new BusinessException("Sick leave limit exceeded");

        if (type == Leave.LeaveType.CASUAL &&
            leaveRepository.findByEmployeeAndTypeAndStatus(employee, type, Leave.LeaveStatus.APPROVED).size() + days > 6)
            throw new BusinessException("Casual leave limit exceeded");

        if (type == Leave.LeaveType.WFH) {
            long used = leaveRepository.findByEmployeeAndTypeAndStatus(employee, type, Leave.LeaveStatus.APPROVED)
                    .stream()
                    .filter(l -> l.getFromDate().with(DayOfWeek.MONDAY)
                            .equals(from.with(DayOfWeek.MONDAY)))
                    .count();
            if (used >= 1)
                throw new BusinessException("Only 1 WFH allowed per week");
        }

        Leave leave = new Leave();
        leave.setEmployee(employee);
        leave.setFromDate(from);
        leave.setToDate(to);
        leave.setType(type);
        leave.setStatus(Leave.LeaveStatus.PENDING);

        return leaveRepository.save(leave);
    }

    @Override
    public Leave approveLeave(Long leaveId) {
        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found"));

        leave.setStatus(Leave.LeaveStatus.APPROVED);
        leaveRepository.save(leave);

        leave.getFromDate().datesUntil(leave.getToDate().plusDays(1))
                .forEach(d ->
                        attendanceService.markAttendanceForLeave(
                                leave.getEmployee(),
                                d,
                                leave.getType() == Leave.LeaveType.WFH
                                        ? Attendance.AttendanceStatus.WFH
                                        : Attendance.AttendanceStatus.LEAVE));

        return leave;
    }

    @Override
    public Leave rejectLeave(Long leaveId) {
        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found"));
        leave.setStatus(Leave.LeaveStatus.REJECTED);
        return leaveRepository.save(leave);
    }

    @Override
    public Map<String, Integer> getLeaveBalance(Employee employee) {
        Map<String, Integer> map = new HashMap<>();
        map.put("SICK_REMAINING", 12 -
                leaveRepository.findByEmployeeAndTypeAndStatus(employee, Leave.LeaveType.SICK, Leave.LeaveStatus.APPROVED).size());
        map.put("CASUAL_REMAINING", 6 -
                leaveRepository.findByEmployeeAndTypeAndStatus(employee, Leave.LeaveType.CASUAL, Leave.LeaveStatus.APPROVED).size());
        return map;
    }
}
