package com.neb.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class JobApplicationDto {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private LocalDate applicationDate;
    private String status;
    private String resumeUrl;
}
