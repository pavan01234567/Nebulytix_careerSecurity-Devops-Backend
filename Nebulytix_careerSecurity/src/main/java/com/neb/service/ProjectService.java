package com.neb.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;


import com.neb.dto.ProjectResponseDto;
import com.neb.dto.ResponseMessage;
import com.neb.dto.UpdateProjectRequestDto;
import com.neb.dto.project.AddProjectRequestDto;
import com.neb.dto.project.ProjectsResponseDto;
import com.neb.entity.Project;
import com.neb.util.ProjectStatus;
public interface ProjectService {
	
	  public Project addProject(
	            AddProjectRequestDto dto,
	            MultipartFile quotation,
	            MultipartFile requirement,
	            MultipartFile contract
	            
	    );

    public ResponseMessage<List<ProjectsResponseDto>> getAllProjects();
    public ResponseMessage<ProjectsResponseDto> getProjectById(Long id);
    public ResponseMessage<ProjectResponseDto> updateProject(Long id, UpdateProjectRequestDto dto);
    public ResponseMessage<String> deleteProject(Long id);
    ProjectResponseDto updateProjectStatus(Long projectId, ProjectStatus status);
    public List<ProjectResponseDto> getProjectsByClient(Long clientId);
    public  ProjectResponseDto addEmployeeToProject(Long projectId, Long employeeId);
    public void removeEmployeeFromProject(Long projectId, Long employeeId);
    public ProjectsResponseDto getActiveProjectsByEmployee(Long employeeId);
    public List<ProjectsResponseDto> getProjectsByEmployeeId(Long employeeId);
}
