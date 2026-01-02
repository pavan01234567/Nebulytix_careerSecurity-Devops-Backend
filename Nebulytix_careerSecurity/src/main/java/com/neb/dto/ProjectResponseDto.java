package com.neb.dto;

import java.time.LocalDate;
import java.util.List;

import com.neb.dto.client.ClientProfileDto;
import com.neb.dto.employee.EmployeeProfileDto;
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
    
    // Converts Project entity to DTO
    public static ProjectResponseDto fromEntity(Project project) {
        if (project == null) return null;

        ProjectResponseDto dto = new ProjectResponseDto();
        dto.setId(project.getId());
        dto.setProjectName(project.getProjectName());
        dto.setProjectCode(project.getProjectCode());
        dto.setProjectType(project.getProjectType());
        dto.setDescription(project.getDescription());
        dto.setStartDate(project.getStartDate());
        dto.setExpectedEndDate(project.getExpectedEndDate());
        dto.setPriority(project.getPriority());
        dto.setBudget(project.getBudget());
        dto.setRiskLevel(project.getRiskLevel());
        dto.setStatus(project.getStatus());
        dto.setProgress(project.getProgress());
        dto.setClientId(project.getClient() != null ? project.getClient().getId() : null);
        return dto;
    }
}
