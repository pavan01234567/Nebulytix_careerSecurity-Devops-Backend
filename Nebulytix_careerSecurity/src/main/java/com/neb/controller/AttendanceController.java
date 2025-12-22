package com.neb.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

import com.neb.entity.Attendance;
import com.neb.entity.Employee;
import com.neb.service.AttendanceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/login/{employeeId}")
    public Attendance login(@PathVariable Long employeeId) {
        Employee e = new Employee();
        e.setId(employeeId);
        return attendanceService.login(e);
    }

    @PostMapping("/logout/{employeeId}")
    public Attendance logout(@PathVariable Long employeeId) {
        Employee e = new Employee();
        e.setId(employeeId);
        return attendanceService.logout(e);
    }

    @GetMapping("/monthly-summary/{employeeId}")
    public Map<String, Long> summary(@PathVariable Long employeeId,
                                     @RequestParam int year,
                                     @RequestParam int month) {
        Employee e = new Employee();
        e.setId(employeeId);
        return attendanceService.getMonthlySummary(e, year, month);
    }

    @GetMapping("/hr/monthly-all")
    public List<Attendance> allEmployees(@RequestParam int year, @RequestParam int month) {
        return attendanceService.getAllEmployeesMonthlyAttendance(year, month);
    }
}
