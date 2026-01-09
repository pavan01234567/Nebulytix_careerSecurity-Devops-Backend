package com.neb.dto;

import java.time.LocalDate;

import com.neb.util.ProjectStatus;

import lombok.Data;

@Data
public class UpdateProjectRequestDto {
    private String projectName;
    private String description;
    private LocalDate expectedEndDate;
    private String priority;
    private String riskLevel;
    private ProjectStatus status;
}
