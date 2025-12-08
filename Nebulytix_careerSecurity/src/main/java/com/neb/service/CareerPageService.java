package com.neb.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.neb.dto.AddJobApplicationRequestDto;
import com.neb.dto.AddJobApplicationResponseDto;
import com.neb.dto.JobApplicationDto;
import com.neb.dto.JobDetailsDto;

public interface CareerPageService {

	public JobDetailsDto getJobById(Long id);
	public AddJobApplicationResponseDto applyForJob(AddJobApplicationRequestDto requestDto, MultipartFile resume);
	public List<JobApplicationDto> getApplicationsByJobId(Long jobId);
}
