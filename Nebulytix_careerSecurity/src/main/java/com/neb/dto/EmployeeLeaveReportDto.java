package com.neb.dto;


import java.time.Month;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeLeaveReportDto {
	
	private Long id;
	
	private String employeeId;
	
	private Integer casualLeaves;
	
	private Integer workFromHomes;
	
	private Integer sickLeaves;

	private Long daysPresent;
	
	private Month month;
	
	private Integer year;
}
