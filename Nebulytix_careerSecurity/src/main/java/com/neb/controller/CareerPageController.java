package com.neb.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.neb.dto.AddJobApplicationRequestDto;
import com.neb.dto.AddJobApplicationResponseDto;
import com.neb.dto.JobApplicationDto;
import com.neb.dto.JobDetailsDto;
import com.neb.dto.ResponseMessage;
import com.neb.service.CareerPageService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/career")
public class CareerPageController {

    @Autowired
    private CareerPageService service;

    @GetMapping("/job/{id}")
    public ResponseEntity<ResponseMessage<JobDetailsDto>> getJobById(@PathVariable("id") Long id) {
        JobDetailsDto job = service.getJobById(id);
        if (job == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseMessage<JobDetailsDto>(
                            HttpStatus.NOT_FOUND.value(),
                            HttpStatus.NOT_FOUND.name(),
                            "Job not found"));
        }
        return ResponseEntity.ok(
                new ResponseMessage<JobDetailsDto>(
                        HttpStatus.OK.value(),
                        HttpStatus.OK.name(),
                        "Job fetched successfully",
                        job));
    }

    @PostMapping(path = "/applyJob", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseMessage<AddJobApplicationResponseDto>> applyForJob(
            @RequestPart("data") AddJobApplicationRequestDto requestDto,
            @RequestPart("resume") MultipartFile resume) {

        AddJobApplicationResponseDto resp = service.applyForJob(requestDto, resume);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseMessage<AddJobApplicationResponseDto>(
                        HttpStatus.CREATED.value(),
                        HttpStatus.CREATED.name(),
                        "Application submitted successfully",
                        resp));
    }
    
    @GetMapping("/job/{jobId}/applications")
    public ResponseEntity<ResponseMessage<List<JobApplicationDto>>> getApplicationsByJob(
            @PathVariable Long jobId) {

        List<JobApplicationDto> applications = service.getApplicationsByJobId(jobId);

        return ResponseEntity.ok(
                new ResponseMessage<>(
                        HttpStatus.OK.value(),
                        HttpStatus.OK.name(),
                        "Applications fetched successfully",
                        applications
                )
        );
    }  
}
