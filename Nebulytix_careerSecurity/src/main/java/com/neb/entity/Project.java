package com.neb.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.neb.util.ProjectStatus;
import com.neb.util.ProjectStatusConverter;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "projects")
//@Data
@Setter
@Getter
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

//@Enumerated(EnumType.STRING)
//private ProjectStatus status; 
    // planned, ongoing, on-hold, completed, cancelled
    @Convert(converter = ProjectStatusConverter.class)
    private ProjectStatus status;

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
    
    // PROJECT DOCUMENTS
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectDocument> documents = new ArrayList<>();
}
