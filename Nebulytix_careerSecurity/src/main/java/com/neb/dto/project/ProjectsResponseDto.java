package com.neb.dto.project;

import java.time.LocalDate;
import java.util.List;

import com.neb.dto.EmployeeResponseDto;
import com.neb.dto.client.ClientProfileDto;
import com.neb.dto.employee.EmployeeProfileDto;
import com.neb.entity.Project;
import com.neb.util.ProjectStatus;

import lombok.Data;

@Data
public class ProjectsResponseDto {
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
    private ProjectStatus status;
    private Integer progress;
    private Long clientId;
    private String contractPdfUrl;
    private String quotationPdfUrl;
    private String requirementDocUrl;
    private ClientProfileDto client;                // ✅ Client profile
    private List<EmployeeProfileDto> employees; 
    // Converts Project entity to DTO
    public static ProjectsResponseDto fromEntity(Project project) {
        if (project == null) return null;

        ProjectsResponseDto dto = new ProjectsResponseDto();
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
        dto.setQuotationPdfUrl(project.getQuotationPdfUrl());
        dto.setContractPdfUrl(project.getContractPdfUrl());
        dto.setRequirementDocUrl(project.getRequirementDocUrl());
        return dto;
    }

}
