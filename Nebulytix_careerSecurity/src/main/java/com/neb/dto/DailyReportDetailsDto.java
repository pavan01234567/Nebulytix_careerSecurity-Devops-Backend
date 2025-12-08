package com.neb.dto;

import java.time.LocalDate;

import com.neb.entity.Employee;

import lombok.Data;

@Data
public class DailyReportDetailsDto {

	private Long id;
	private Employee employee;
    private LocalDate reportDate;
    private String summary;
}
