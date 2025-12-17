package com.neb.repo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neb.entity.DailyReport;

public interface DailyReportRepository extends JpaRepository<DailyReport, Long> {
	
	List<DailyReport> findByReportDate(LocalDate reportDate);
	Optional<DailyReport> findByEmployeeIdAndReportDate(Long employeeId, LocalDate reportDate);
	 List<DailyReport> findByEmployee_Project_Id(Long projectId);
}
