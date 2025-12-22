package com.neb.repo;



import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neb.entity.Attendance;
import com.neb.entity.Employee;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByEmployeeAndDate(Employee employee, LocalDate date);

    List<Attendance> findByEmployeeAndDateBetween(
            Employee employee, LocalDate start, LocalDate end);

    List<Attendance> findByDateBetween(LocalDate start, LocalDate end);
}
