package com.neb.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class JobDetailsDto {

	private Long id;
	private String jobTitle;//java developer,intern,hr
    private String domain;//java,python,.net
    private String jobType;// FULL_TIME, PART_TIME, CONTRACT
    private String experienceLevel;// ENTRY, MID, SENIOR
    private String description;
    private String requirements;
    private String responsibilities;
    private String salaryRange;
    private Boolean isActive;
    private LocalDate postedDate;
    private LocalDate closingDate;
    private String location;
}
