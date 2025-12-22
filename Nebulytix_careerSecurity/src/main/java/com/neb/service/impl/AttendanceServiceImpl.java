package com.neb.service.impl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neb.entity.Attendance;
import com.neb.entity.Employee;
import com.neb.exception.BusinessException;
import com.neb.repo.AttendanceRepository;
import com.neb.service.AttendanceService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;

    @Override
    public Attendance login(Employee employee) {
        Attendance attendance = attendanceRepository
                .findByEmployeeAndDate(employee, LocalDate.now())
                .orElseGet(() -> {
                    Attendance a = new Attendance();
                    a.setEmployee(employee);
                    a.setDate(LocalDate.now());
                    a.setStatus(Attendance.AttendanceStatus.PRESENT);
                    return a;
                });

        attendance.setLoginTime(LocalTime.now());
        return attendanceRepository.save(attendance);
    }

    @Override
    public Attendance logout(Employee employee) {
        Attendance attendance = attendanceRepository
                .findByEmployeeAndDate(employee, LocalDate.now())
                .orElseThrow(() -> new BusinessException("Please login before logout"));

        attendance.setLogoutTime(LocalTime.now());
        return attendanceRepository.save(attendance);
    }

    @Override
    public void markAttendanceForLeave(Employee employee, LocalDate date,
                                       Attendance.AttendanceStatus status) {
        Attendance attendance = attendanceRepository
                .findByEmployeeAndDate(employee, date)
                .orElseGet(() -> {
                    Attendance a = new Attendance();
                    a.setEmployee(employee);
                    a.setDate(date);
                    return a;
                });
        attendance.setStatus(status);
        attendanceRepository.save(attendance);
    }

    @Override
    public Map<String, Long> getMonthlySummary(Employee employee, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Attendance> list =
                attendanceRepository.findByEmployeeAndDateBetween(employee, start, end);

        Map<String, Long> map = new HashMap<>();
        map.put("PRESENT", list.stream().filter(a -> a.getStatus() == Attendance.AttendanceStatus.PRESENT).count());
        map.put("LEAVE", list.stream().filter(a -> a.getStatus() == Attendance.AttendanceStatus.LEAVE).count());
        map.put("WFH", list.stream().filter(a -> a.getStatus() == Attendance.AttendanceStatus.WFH).count());
        map.put("HALF_DAY", list.stream().filter(a -> a.getStatus() == Attendance.AttendanceStatus.HALF_DAY).count());

        return map;
    }

    @Override
    public List<Attendance> getEmployeeMonthlyAttendance(Employee employee, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return attendanceRepository.findByEmployeeAndDateBetween(employee, start, end);
    }

    @Override
    public List<Attendance> getAllEmployeesMonthlyAttendance(int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return attendanceRepository.findByDateBetween(start, end);
    }
}
