package com.neb.service.impl;



import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.neb.dto.ProjectResponseDto;
import com.neb.dto.ResponseMessage;
import com.neb.dto.UpdateProjectRequestDto;
import com.neb.dto.project.AddProjectRequestDto;
import com.neb.entity.Client;
import com.neb.entity.Project;
import com.neb.entity.ProjectDocument;
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
private  ProjectRepository projectRepository;
@Autowired
private ProjectDocumentRepository docRepo;

@Value("${file.upload-dir}")
private String uploadDir;

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

        project.setStatus(status); // Update status
        projectRepository.save(project);

        return ProjectResponseDto.fromEntity(project);
    }
    
    @Override
    public List<ProjectResponseDto> getProjectsByClient(Long clientId) {

        // Fetch projects by client ID, throw exception if client not found
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
            MultipartFile requirement,
            MultipartFile contract,
            List<MultipartFile> documents) {

        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id " + dto.getClientId()));

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

        // Store mandatory files
        project.setQuotationPdfUrl(storeFile(quotation));
        project.setRequirementDocUrl(storeFile(requirement));
        project.setContractPdfUrl(storeFile(contract));

        Project savedProject = projectRepository.save(project);

        // Store optional documents
        if (documents != null && !documents.isEmpty()) {
            for (MultipartFile file : documents) {
                ProjectDocument doc = new ProjectDocument();
                doc.setFileName(file.getOriginalFilename());
                doc.setFileUrl(storeFile(file));
                doc.setProject(savedProject);
                docRepo.save(doc);
            }
        }

        return savedProject;
    }

    /**
     * Store a single file safely in uploadDir
     */
    private String storeFile(MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                throw new FileStorageException("Cannot store empty file");
            }

            System.out.println("Uploading file: " + file.getOriginalFilename());
            System.out.println("Upload directory: " + uploadDir);

            // Sanitize file name: remove spaces and special characters
            String safeFileName = file.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");

            // Ensure upload directory exists
            Path uploadPath = Path.of(uploadDir);
            Files.createDirectories(uploadPath);

            // Create final path with timestamp to avoid conflicts
            Path path = uploadPath.resolve(System.currentTimeMillis() + "_" + safeFileName);

            // Write file bytes
            Files.write(path, file.getBytes());

            System.out.println("File uploaded successfully: " + path.toString());
            return path.toString();

        } catch (Exception e) {
            e.printStackTrace();
            throw new FileStorageException("Failed to upload file: " + file.getOriginalFilename());
        }
    }
    
	}


