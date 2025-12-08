//Job Entity
package com.neb.entity;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String jobTitle;//java developer,intern,hr
    private String domain;//java,python,.net
    private String jobType;// FULL_TIME, PART_TIME, CONTRACT
    private String experienceLevel;// ENTRY, MID, SENIOR
    
    @Column(length=4000)
    private String description;
    @Column(length=4000)
    private String requirements;
    
    @Column(length=4000)
    private String responsibilities;
    private String salaryRange;
    private Boolean isActive;
    private LocalDate postedDate;
    private LocalDate closingDate;
    
    private String location;
    
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore //
    private List<JobApplication> applications;
}
