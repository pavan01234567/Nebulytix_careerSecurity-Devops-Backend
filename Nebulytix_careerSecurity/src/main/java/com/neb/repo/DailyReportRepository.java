package com.neb.repo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.neb.entity.DailyReport;

public interface DailyReportRepository extends JpaRepository<DailyReport, Long> {
    List<DailyReport> findByReportDate(LocalDate reportDate);
	List<DailyReport> findByEmployee_Project_Id(Long projectId);
	 @Query("""
		        SELECT dr
		        FROM DailyReport dr
		        JOIN FETCH dr.employee e
		        WHERE dr.reportDate = :date
		    """)
		    List<DailyReport> findReportsWithEmployee(@Param("date") LocalDate date);
	 
	 
	   Optional<DailyReport> findByEmployeeIdAndReportDate(
	            Long employeeId, LocalDate reportDate);

	    List<DailyReport> findAllByReportDate(LocalDate reportDate);
}
