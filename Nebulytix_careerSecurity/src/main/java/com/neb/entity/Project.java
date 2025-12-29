package com.neb.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "projects")
@Data
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String projectName;
    private String projectCode;
    private String projectType;     // web app, mobile app, CRM, ERP etc.
    private String description;

    private LocalDate startDate;
    private LocalDate expectedEndDate;
    private LocalDate actualEndDate;

    private String priority;        // low, medium, high, critical
    private String status = "planned"; // planned, ongoing, on-hold, completed, cancelled

    private Double budget;
    private Double spentAmount = 0.0;

    private Integer progress = 0;   // 0–100

    // Mandatory fixed documents
    private String quotationPdfUrl;
    private String requirementDocUrl;     // SRS / BRD
    private String contractPdfUrl;        // Agreement document

    private String riskLevel;       // low, medium, high
    private String tags;            // “UI/UX, Backend, API”

    private LocalDate createdDate = LocalDate.now();
    private LocalDate updatedDate;

    // MANY PROJECTS → ONE CLIENT
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    // PROJECT MANAGER = EMPLOYEE WITH ROLE_MANAGER
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Employee projectManager;

    // MANY EMPLOYEES WORK ON MANY PROJECTS
    @ManyToMany
    @JoinTable(
            name = "project_employees",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    private List<Employee> employees = new ArrayList<>();
    
//    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
//    private List<Employee> employees = new ArrayList<>();
//    
   

    // PROJECT DOCUMENTS
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectDocument> documents = new ArrayList<>();
}
