package com.neb.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.neb.dto.AddJobApplicationRequestDto;
import com.neb.dto.AddJobApplicationResponseDto;
import com.neb.dto.JobApplicationDto;
import com.neb.dto.JobDetailsDto;

import com.neb.entity.Job;
import com.neb.entity.JobApplication;
import com.neb.exception.CustomeException;
import com.neb.repo.JobApplicationRepository;
import com.neb.repo.JobRepository;
import com.neb.service.CareerPageService;
import com.neb.service.EmailService;

@Service
public class CareerPageServiceImpl implements CareerPageService {

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private EmailService emailService;

    @Value("${application.resume}")
    private String uploadDir;
    
    @Override
    public JobDetailsDto getJobById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new CustomeException("Job not found with id: " + id));

        LocalDate today = LocalDate.now();
        job.setIsActive(job.getClosingDate() == null || !job.getClosingDate().isBefore(today));

        return mapper.map(job, JobDetailsDto.class);
    }

	@Override
	public AddJobApplicationResponseDto applyForJob(AddJobApplicationRequestDto requestDto, MultipartFile resume) {
		
		 if (requestDto == null) {
	            throw new CustomeException("Request data is missing");
	        }

	        if (resume == null || resume.isEmpty()) {
	            throw new CustomeException("Resume file is required");
	        }
	        
		 Job job = jobRepository.findById(requestDto.getJobId())
	                .orElseThrow(() -> new CustomeException("Job not found with ID: " + requestDto.getJobId()));

	        // Check if user already applied (same email or same domain)
	        boolean exists = jobApplicationRepository.findAll().stream()
	                .anyMatch(app -> app.getEmail().equalsIgnoreCase(requestDto.getEmail()));

	        if (exists) {
	            throw new CustomeException("You already applied for a job with this email!");
	        }
	        
	        JobApplication application = new JobApplication();
	        application.setEmail(requestDto.getEmail());
	        application.setFullName(requestDto.getFullName());
	        application.setPhoneNumber(requestDto.getPhoneNumber());
	        application.setApplicationDate(LocalDate.now());
	        application.setJob(job);	
	        application.setStatus("SUBMITTED");
	               
            try {       	
            	// ensure directory exists
                Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
                Files.createDirectories(uploadPath);

                String originalFilename = StringUtils.cleanPath(resume.getOriginalFilename());
                // optionally add unique suffix
                String fileName = System.currentTimeMillis() + "_" + originalFilename;
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(resume.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                application.setResumeFilePath("/uploads/resumes/" + fileName);

            }catch (IOException ex) {
                throw new CustomeException("Could not store file. Error: " + ex.getMessage());
            }
	        
            JobApplication saveApplication = jobApplicationRepository.save(application);
            
            emailService.sendConfirmationEmail(
                    saveApplication.getEmail(),
                    saveApplication.getFullName(),
                    job.getJobTitle()
            );
            
            AddJobApplicationResponseDto applicationRes = new AddJobApplicationResponseDto();
            
            applicationRes.setId(saveApplication.getId());
            applicationRes.setApplicationDate(saveApplication.getApplicationDate());
            applicationRes.setStatus(saveApplication.getStatus());

		return applicationRes;
	}  
	
	@Override
	public List<JobApplicationDto> getApplicationsByJobId(Long jobId){

	    Job job = jobRepository.findById(jobId)
	            .orElseThrow(() -> new CustomeException("Job not found with ID: " + jobId));

	    List<JobApplication> apps = job.getApplications();
	    
	    List<JobApplicationDto> applicationsResponse = apps.stream().map(app->{
	    	JobApplicationDto dto = new JobApplicationDto();
	        dto.setId(app.getId());
	        dto.setFullName(app.getFullName());
	        dto.setEmail(app.getEmail());
	        dto.setPhoneNumber(app.getPhoneNumber());
	        dto.setApplicationDate(app.getApplicationDate());
	        dto.setStatus(app.getStatus());
	        dto.setResumeUrl(app.getResumeFilePath());
	        
	        return dto;
	    }).collect(Collectors.toList());
	    
	    return applicationsResponse;
	}
}
