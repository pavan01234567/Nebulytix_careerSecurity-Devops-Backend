package com.neb.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class JobApplication {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "JOB_ID", nullable = false)
    private Job job;

    private String fullName;
    private String email;
    private String phoneNumber;
    private String resumeFilePath;
    private LocalDate applicationDate;
    private String status;// SUBMITTED, REVIEWED, INTERVIEW, REJECTED
}
