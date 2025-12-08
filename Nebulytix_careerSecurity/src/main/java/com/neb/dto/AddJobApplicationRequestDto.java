package com.neb.dto;



import lombok.Data;

@Data
public class AddJobApplicationRequestDto {
    private Long jobId;
    private String fullName;
    private String email;
    private String phoneNumber;
}