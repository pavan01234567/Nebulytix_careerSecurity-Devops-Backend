package com.neb.dto;

import java.time.LocalDate;

import com.neb.entity.Project;

import lombok.Data;

@Data
public class ProjectResponseDto {
    private Long id;
    private String projectName;
    private String projectCode;
    private String projectType;
    private String description;
    private LocalDate startDate;
    private LocalDate expectedEndDate;
    private String priority;
    private Double budget;
    private String riskLevel;
    private String status;
    private Integer progress;
    private Long clientId;
	public static ProjectResponseDto fromEntity(Project project) {
		// TODO Auto-generated method stub
		return null;
	}
}
