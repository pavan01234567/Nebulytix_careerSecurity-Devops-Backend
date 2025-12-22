package com.neb.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.neb.entity.Attendance;
import com.neb.entity.Employee;

public interface AttendanceService {

    Attendance login(Employee employee);

    Attendance logout(Employee employee);

    void markAttendanceForLeave(Employee employee, LocalDate date,
                                Attendance.AttendanceStatus status);

    Map<String, Long> getMonthlySummary(Employee employee, int year, int month);

    List<Attendance> getEmployeeMonthlyAttendance(Employee employee, int year, int month);

    List<Attendance> getAllEmployeesMonthlyAttendance(int year, int month);
}
