package com.neb.entity;



import java.time.LocalDate;
import com.neb.util.EmployeeLeaveType; // ✔ correct package



import com.neb.util.ApprovalStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeLeaves {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee_id",nullable = false)//,referencedColumnName = "employeeId")
	private Employee employee;
	
	@Enumerated(EnumType.STRING)
	private EmployeeLeaveType leaveType;
	
	@Enumerated(EnumType.STRING)
	private ApprovalStatus leaveStatus = ApprovalStatus.PENDING;
	
	private LocalDate startDate;
	private LocalDate endDate;
	private String reason;
	private Long totalDays;
	private Integer currentYear;
	private Integer currentMonth;
	private LocalDate appliedDate;
	

}
