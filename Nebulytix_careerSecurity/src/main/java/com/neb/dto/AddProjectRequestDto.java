package com.neb.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AddProjectRequestDto {
    private Long clientId;
    private String projectName;
    private String projectCode;
    private String projectType;
    private String description;
    private LocalDate startDate;
    private LocalDate expectedEndDate;
    private String priority;
    private Double budget;
    private String riskLevel;

    // Add document URLs
    private String quotationPdfUrl;
    private String requirementDocUrl;
    private String contractPdfUrl;
}
