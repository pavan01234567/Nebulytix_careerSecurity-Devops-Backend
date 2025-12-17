package com.neb.service;

import java.util.List;

import com.neb.dto.AddProjectRequestDto;
import com.neb.dto.ProjectResponseDto;
import com.neb.dto.ResponseMessage;
import com.neb.dto.UpdateProjectRequestDto;
import com.neb.entity.Project;

public interface ProjectService {
    Project addProject(AddProjectRequestDto dto);

    ResponseMessage<List<ProjectResponseDto>> getAllProjects();

    ResponseMessage<ProjectResponseDto> getProjectById(Long id);

    ResponseMessage<ProjectResponseDto> updateProject(Long id, UpdateProjectRequestDto dto);

    ResponseMessage<String> deleteProject(Long id);
    public ProjectResponseDto updateProjectStatus(Long projectId, String status);
}
