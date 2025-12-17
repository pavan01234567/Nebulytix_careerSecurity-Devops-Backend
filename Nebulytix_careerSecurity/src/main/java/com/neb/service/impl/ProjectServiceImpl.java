package com.neb.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.neb.dto.AddProjectRequestDto;
import com.neb.dto.ProjectResponseDto;
import com.neb.dto.ResponseMessage;
import com.neb.dto.UpdateProjectRequestDto;
import com.neb.entity.Client;
import com.neb.entity.Project;
import com.neb.entity.ProjectDocument;
import com.neb.repo.ClientRepository;
import com.neb.repo.ProjectRepository;
import com.neb.service.ProjectService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {
@Autowired
   private ClientRepository clientRepository;
@Autowired
private  ProjectRepository projectRepository;

    @Override
    public Project addProject(AddProjectRequestDto dto) {

        // Fetch client
        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new RuntimeException("Client not found"));

        // Create Project entity
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
        project.setStatus("planned"); // default
        project.setProgress(0);
        project.setCreatedDate(LocalDate.now());

        // Example: Add ProjectDocument(s) if URLs are provided
        List<ProjectDocument> documents = new ArrayList<>();
        if (dto.getQuotationPdfUrl() != null) {
            ProjectDocument quotation = new ProjectDocument();
            quotation.setFileName("Quotation.pdf");
            quotation.setFileUrl(dto.getQuotationPdfUrl());
            quotation.setProject(project);
            documents.add(quotation);
        }

        if (dto.getRequirementDocUrl() != null) {
            ProjectDocument reqDoc = new ProjectDocument();
            reqDoc.setFileName("RequirementDoc.pdf");
            reqDoc.setFileUrl(dto.getRequirementDocUrl());
            reqDoc.setProject(project);
            documents.add(reqDoc);
        }

        if (dto.getContractPdfUrl() != null) {
            ProjectDocument contract = new ProjectDocument();
            contract.setFileName("Contract.pdf");
            contract.setFileUrl(dto.getContractPdfUrl());
            contract.setProject(project);
            documents.add(contract);
        }

        project.setDocuments(documents);

        // Save project (cascade will save documents)
        return projectRepository.save(project);
    }
    
    @Override
    public ResponseMessage<List<ProjectResponseDto>> getAllProjects() {
        List<ProjectResponseDto> dtoList =
                projectRepository.findAll().stream().map(this::map).toList();
        return new ResponseMessage<>(200, "SUCCESS", "All projects", dtoList);
    }

    @Override
    public ResponseMessage<ProjectResponseDto> getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        return new ResponseMessage<>(200, "SUCCESS", "Project details", map(project));
    }

    @Override
    public ResponseMessage<ProjectResponseDto> updateProject(Long id, UpdateProjectRequestDto dto) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

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
    public ProjectResponseDto updateProjectStatus(Long projectId, String status) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));

        project.setStatus(status); // Update status
        projectRepository.save(project);

        return ProjectResponseDto.fromEntity(project);
    }
}
