package com.neb.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

import com.neb.entity.Employee;
import com.neb.entity.Leave;
import com.neb.service.LeaveService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    @PostMapping("/apply/{employeeId}")
    public Leave apply(@PathVariable Long employeeId,
                       @RequestParam LocalDate from,
                       @RequestParam LocalDate to,
                       @RequestParam Leave.LeaveType type) {
        Employee e = new Employee();
        e.setId(employeeId);
        return leaveService.applyLeave(e, from, to, type);
    }

    @PostMapping("/approve/{leaveId}")
    public Leave approve(@PathVariable Long leaveId) {
        return leaveService.approveLeave(leaveId);
    }

    @PostMapping("/reject/{leaveId}")
    public Leave reject(@PathVariable Long leaveId) {
        return leaveService.rejectLeave(leaveId);
    }

    @GetMapping("/balance/{employeeId}")
    public Map<String, Integer> balance(@PathVariable Long employeeId) {
        Employee e = new Employee();
        e.setId(employeeId);
        return leaveService.getLeaveBalance(e);
    }
}
