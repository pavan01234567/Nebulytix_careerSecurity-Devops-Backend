package com.neb.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class AddDailyReportRequestDto {

	private Long employee_id;
    private LocalDate reportDate;
    private String summary;
	
}
