package com.neb.dto;

import com.neb.constants.WorkStatus;

import lombok.Data;

@Data
public class SubmitTaskReportDto 
{
	private WorkStatus status;
    private String reportDetails;
}
