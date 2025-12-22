package com.neb.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.neb.dto.ProjectResponseDto;
import com.neb.dto.ResponseMessage;
import com.neb.dto.UpdateProjectRequestDto;
import com.neb.dto.project.AddProjectRequestDto;
import com.neb.entity.Project;
public interface ProjectService {
	
	  Project addProject(
	            AddProjectRequestDto dto,
	            MultipartFile quotation,
	            MultipartFile requirement
	            
	    );

    ResponseMessage<List<ProjectResponseDto>> getAllProjects();

    ResponseMessage<ProjectResponseDto> getProjectById(Long id);

    ResponseMessage<ProjectResponseDto> updateProject(Long id, UpdateProjectRequestDto dto);

    ResponseMessage<String> deleteProject(Long id);
    public ProjectResponseDto updateProjectStatus(Long projectId, String status);
    List<ProjectResponseDto> getProjectsByClient(Long clientId);
}
