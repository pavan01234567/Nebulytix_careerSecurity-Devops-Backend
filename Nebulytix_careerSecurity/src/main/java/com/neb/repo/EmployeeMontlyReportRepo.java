package com.neb.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.neb.entity.Employee;
import com.neb.entity.EmployeeMonthlyReport;

public interface EmployeeMontlyReportRepo extends JpaRepository<EmployeeMonthlyReport, Long> {

    Optional<EmployeeMonthlyReport> findByEmployee_IdAndCurrentYearAndCurrentMonth(
            Long employeeId, Integer year, Integer month);

    List<EmployeeMonthlyReport> findByCurrentYearAndCurrentMonth(Integer year, Integer month);

    Optional<EmployeeMonthlyReport> findByEmployeeAndCurrentYearAndCurrentMonth(Employee employee, int year, int month);

    Optional<EmployeeMonthlyReport> findByIdAndCurrentYearAndCurrentMonth(Long id, int year, int month);

    @Query("SELECT emr FROM EmployeeMonthlyReport emr WHERE emr.employee.id = :empId AND emr.currentYear = :year AND emr.currentMonth = :month")
    Optional<EmployeeMonthlyReport> findReport(@Param("empId") Long employeeId, 
                                               @Param("year") Integer year, 
                                               @Param("month") Integer month);
}
