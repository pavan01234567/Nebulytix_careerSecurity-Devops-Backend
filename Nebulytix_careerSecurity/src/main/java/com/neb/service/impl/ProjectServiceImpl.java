package com.neb.service.impl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.neb.dto.ProjectResponseDto;
import com.neb.dto.ResponseMessage;
import com.neb.dto.UpdateProjectRequestDto;
import com.neb.dto.project.AddProjectRequestDto;
import com.neb.entity.Client;
import com.neb.entity.Project;
import com.neb.exception.CustomeException;
import com.neb.exception.FileStorageException;
import com.neb.exception.ResourceNotFoundException;
import com.neb.repo.ClientRepository;
import com.neb.repo.ProjectDocumentRepository;
import com.neb.repo.ProjectRepository;
import com.neb.service.ProjectService;

import jakarta.transaction.Transactional;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectDocumentRepository docRepo;

    // ✅ CHANGED: hardcoded projects directory (NO application.properties needed)
    private static final String PROJECTS_DIR = "projects";

    @Override
    public ResponseMessage<List<ProjectResponseDto>> getAllProjects() {
        List<ProjectResponseDto> dtoList =
                projectRepository.findAll().stream().map(this::map).toList();
        return new ResponseMessage<>(200, "SUCCESS", "All projects", dtoList);
    }

    @Override
    public ResponseMessage<ProjectResponseDto> getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new CustomeException("Project not found"));
        return new ResponseMessage<>(200, "SUCCESS", "Project details", map(project));
    }

    @Override
    public ResponseMessage<ProjectResponseDto> updateProject(Long id, UpdateProjectRequestDto dto) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new CustomeException("Project not found"));

        if (dto.getProjectName() != null) project.setProjectName(dto.getProjectName());
        if (dto.getDescription() != null) project.setDescription(dto.getDescription());
        if (dto.getExpectedEndDate() != null) project.setExpectedEndDate(dto.getExpectedEndDate());
        if (dto.getPriority() != null) project.setPriority(dto.getPriority());
        if (dto.getRiskLevel() != null) project.setRiskLevel(dto.getRiskLevel());
        if (dto.getStatus() != null) project.setStatus(dto.getStatus());

        projectRepository.save(project);
        return new ResponseMessage<>(200, "SUCCESS", "Project updated", map(project));
    }

    @Override
    public ResponseMessage<String> deleteProject(Long id) {
        projectRepository.deleteById(id);
        return new ResponseMessage<>(200, "SUCCESS", "Project deleted", null);
    }

    private ProjectResponseDto map(Project p) {
        ProjectResponseDto dto = new ProjectResponseDto();
        dto.setId(p.getId());
        dto.setProjectName(p.getProjectName());
        dto.setProjectCode(p.getProjectCode());
        dto.setProjectType(p.getProjectType());
        dto.setDescription(p.getDescription());
        dto.setStartDate(p.getStartDate());
        dto.setExpectedEndDate(p.getExpectedEndDate());
        dto.setPriority(p.getPriority());
        dto.setBudget(p.getBudget());
        dto.setRiskLevel(p.getRiskLevel());
        dto.setStatus(p.getStatus());
        dto.setProgress(p.getProgress());
        dto.setClientId(p.getClient().getId());
        return dto;
    }

    @Override
    @Transactional
    public ProjectResponseDto updateProjectStatus(Long projectId, String status) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomeException("Project not found with id: " + projectId));

        project.setStatus(status);
        projectRepository.save(project);

        return ProjectResponseDto.fromEntity(project);
    }

    @Override
    public List<ProjectResponseDto> getProjectsByClient(Long clientId) {
        List<Project> projects = projectRepository.findByClientId(clientId);

        if (projects.isEmpty()) {
            throw new CustomeException("No projects found for client with ID: " + clientId);
        }

        return projects.stream()
                .map(ProjectResponseDto::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public Project addProject(
            AddProjectRequestDto dto,
            MultipartFile quotation,
            MultipartFile requirement) {

        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Client not found with id " + dto.getClientId()));

        Project project = new Project();
        project.setClient(client);
        project.setProjectName(dto.getProjectName());
        project.setProjectCode(dto.getProjectCode());
        project.setProjectType(dto.getProjectType());
        project.setDescription(dto.getDescription());
        project.setStartDate(dto.getStartDate());
        project.setExpectedEndDate(dto.getExpectedEndDate());
        project.setPriority(dto.getPriority());
        project.setBudget(dto.getBudget());
        project.setRiskLevel(dto.getRiskLevel());
        project.setStatus("PLANNED");
        project.setProgress(0);
        project.setCreatedDate(LocalDate.now());

        // ✅ Files stored in projects/
        project.setQuotationPdfUrl(storeFile(quotation));
        project.setRequirementDocUrl(storeFile(requirement));

        return projectRepository.save(project);
    }

    /**
     * ✅ Store file inside projects/ directory
     */
    private String storeFile(MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                throw new FileStorageException("Cannot store empty file");
            }

            // Sanitize filename
            String safeFileName =
                    file.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");

            // ✅ CHANGED: always use projects directory
            Path projectsPath = Path.of(PROJECTS_DIR);
            Files.createDirectories(projectsPath);

            Path finalPath =
                    projectsPath.resolve(System.currentTimeMillis() + "_" + safeFileName);

            Files.write(finalPath, file.getBytes());

            return finalPath.toString();

        } catch (Exception e) {
            throw new FileStorageException(
                    "Failed to upload file: " + file.getOriginalFilename(), e);
        }
    }
}
