package com.neb.service.impl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.neb.dto.EmployeeResponseDto;
import com.neb.dto.ProjectResponseDto;
import com.neb.dto.ResponseMessage;
import com.neb.dto.UpdateProjectRequestDto;
import com.neb.dto.client.ClientProfileDto;
import com.neb.dto.employee.EmployeeProfileDto;
import com.neb.dto.project.AddProjectRequestDto;
import com.neb.dto.project.ProjectsResponseDto;
import com.neb.entity.Client;
import com.neb.entity.Employee;
import com.neb.entity.Project;
import com.neb.exception.CustomeException;
import com.neb.exception.EmployeeNotFoundException;
import com.neb.exception.FileStorageException;
import com.neb.exception.ResourceNotFoundException;
import com.neb.repo.ClientRepository;
import com.neb.repo.EmployeeRepository;
import com.neb.repo.ProjectDocumentRepository;
import com.neb.repo.ProjectRepository;
import com.neb.service.ClientService;
import com.neb.service.ProjectService;
//import com.neb.dto.project.ProjectsResponseDto;

import jakarta.transaction.Transactional;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectDocumentRepository docRepo;
    
    @Autowired 
    private EmployeeRepository empRepo;
    
    @Autowired
    private ClientService clientService; 
    
    @Autowired
    private ModelMapper mapper;

    // ✅ CHANGED: hardcoded projects directory (NO application.properties needed)
    private static final String PROJECTS_DIR = "projects";
    

    @Override
    public ResponseMessage<List<ProjectsResponseDto>> getAllProjects() {
    	List<Project> projects = projectRepository.findAll();

        List<ProjectsResponseDto> dtoList = projects.stream()
                .map(project -> {

                    ProjectsResponseDto dto = new ProjectsResponseDto();

                    // 🔹 Project fields
                    dto.setId(project.getId());
                    dto.setProjectName(project.getProjectName());
                    dto.setProjectCode(project.getProjectCode());
                    dto.setProjectType(project.getProjectType());
                    dto.setDescription(project.getDescription());
                    dto.setStartDate(project.getStartDate());
                    dto.setExpectedEndDate(project.getExpectedEndDate());
                    dto.setPriority(project.getPriority());
                    dto.setBudget(project.getBudget());
                    dto.setRiskLevel(project.getRiskLevel());
                    dto.setStatus(project.getStatus());
                    dto.setProgress(project.getProgress());
                    dto.setQuotationPdfUrl(project.getQuotationPdfUrl());
                    dto.setContractPdfUrl(project.getContractPdfUrl());
                    dto.setRequirementDocUrl(project.getRequirementDocUrl());

                    // ✅ CLIENT MAPPING
                    if (project.getClient() != null) {
                        Client client = project.getClient();

                        ClientProfileDto clientDto = new ClientProfileDto();
                        clientDto.setId(client.getId());
                        clientDto.setCompanyName(client.getCompanyName());
                        clientDto.setContactPerson(client.getContactPerson());
                        clientDto.setContactEmail(client.getContactEmail());
                        clientDto.setPhone(client.getPhone());
                        clientDto.setAlternatePhone(client.getAlternatePhone());
                        clientDto.setAddress(client.getAddress());
                        clientDto.setWebsite(client.getWebsite());
                        clientDto.setIndustryType(client.getIndustryType());
                        clientDto.setGstNumber(client.getGstNumber());
                        clientDto.setEmpStatus(client.getStatus());
                        clientDto.setUserEnabled(
                                client.getUser() != null && client.getUser().isEnabled()
                        );

                        dto.setClient(clientDto);
                    }

                    
                    if (project.getEmployees() != null && !project.getEmployees().isEmpty()) {
                        List<EmployeeProfileDto> employeeDtos =
                                clientService.getEmployeesByProject(project.getId());
                        dto.setEmployees(employeeDtos);
                    }

                    return dto;
                })
                .toList();
        return new ResponseMessage<>(200, "SUCCESS", "All projects", dtoList);
    }

    @Override
    public ResponseMessage<ProjectsResponseDto> getProjectById(Long id) {
    	 Project project = projectRepository.findProjectWithClientAndEmployees(id)
    	            .orElseThrow(() -> new CustomeException("Project not found"));

    	    ProjectsResponseDto dto = new ProjectsResponseDto();

    	    // 🔹 Project fields
    	    dto.setId(project.getId());
    	    dto.setProjectName(project.getProjectName());
    	    dto.setProjectCode(project.getProjectCode());
    	    dto.setProjectType(project.getProjectType());
    	    dto.setDescription(project.getDescription());
    	    dto.setStartDate(project.getStartDate());
    	    dto.setExpectedEndDate(project.getExpectedEndDate());
    	    dto.setPriority(project.getPriority());
    	    dto.setBudget(project.getBudget());
    	    dto.setRiskLevel(project.getRiskLevel());
    	    dto.setStatus(project.getStatus());
    	    dto.setProgress(project.getProgress());
    	    dto.setQuotationPdfUrl(project.getQuotationPdfUrl());
    	    dto.setContractPdfUrl(project.getContractPdfUrl());
    	    dto.setRequirementDocUrl(project.getRequirementDocUrl());

    	    // ✅ CLIENT MAPPING (NO fromEntity)
    	    if (project.getClient() != null) {
    	        Client client = project.getClient();

    	        ClientProfileDto clientDto = new ClientProfileDto();
    	        clientDto.setId(client.getId());
    	        clientDto.setCompanyName(client.getCompanyName());
    	        clientDto.setContactPerson(client.getContactPerson());
    	        clientDto.setContactEmail(client.getContactEmail());
    	        clientDto.setPhone(client.getPhone());
    	        clientDto.setAlternatePhone(client.getAlternatePhone());
    	        clientDto.setAddress(client.getAddress());
    	        clientDto.setWebsite(client.getWebsite());
    	        clientDto.setIndustryType(client.getIndustryType());
    	        clientDto.setGstNumber(client.getGstNumber());
    	        clientDto.setEmpStatus(client.getStatus());
    	        clientDto.setUserEnabled(client.getUser().isEnabled());

    	        dto.setClient(clientDto);
    	    }

    	    // ✅ EMPLOYEE LIST MAPPING (NO fromEntity)
    	    if (project.getEmployees() != null && !project.getEmployees().isEmpty()) {
    	        List<EmployeeProfileDto> employeeDtos = clientService.getEmployeesByProject(id);
    	        dto.setEmployees(employeeDtos);
    	    }
        return new ResponseMessage<>(200, "SUCCESS", "Project details", dto);
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

        if (projects.isEmpty()) { throw new CustomeException("No projects found for client with ID: " + clientId);}

        return projects.stream().map(ProjectResponseDto::fromEntity).toList();
    }

    @Override
    @Transactional
    public Project addProject(
            AddProjectRequestDto dto,
            MultipartFile quotation,
            MultipartFile requirement,MultipartFile contract) {

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
        
        project.setRequirementDocUrl(storeFile(contract));
        project.setContractPdfUrl(storeFile(contract));

        return projectRepository.save(project);
    }
    
   //  Store file inside projects/ directory
   private String storeFile(MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                throw new FileStorageException("Cannot store empty file");
            }

            // Sanitize filename
            String safeFileName =
                    file.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");

            // CHANGED: always use projects directory
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

	@Override
	public ProjectResponseDto addEmployeeToProject(Long projectId, Long employeeId) {
		    Project project = projectRepository.findById(projectId)
	                .orElseThrow(() -> new CustomeException("Project not found"));

	        Employee employee = empRepo.findById(employeeId)
	                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));
	        System.out.println(employee);
	        if (project.getEmployees().contains(employee)) {
	            throw new CustomeException("Employee already assigned to this project");
	        }

	        // maintain BOTH sides
	        project.getEmployees().add(employee);
	        employee.getAssignedProjects().add(project);
	        employee.setProject(project);

	        Project savedProject = projectRepository.save(project); // owning side
       return mapper.map(savedProject, ProjectResponseDto.class);
       
	}

	@Override
	public void removeEmployeeFromProject(Long projectId, Long employeeId) {
		 Project project = projectRepository.findById(projectId)
	                .orElseThrow(() -> new RuntimeException("Project not found"));

	        Employee employee = empRepo.findById(employeeId)
	                .orElseThrow(() -> new RuntimeException("Employee not found"));

	        // Check assignment
	        if (!project.getEmployees().contains(employee)) {
	            throw new RuntimeException("Employee is not assigned to this project");
	        }

	        // Remove from both sides (VERY IMPORTANT)
	        project.getEmployees().remove(employee);
	        employee.getAssignedProjects().remove(project);
	        employee.setProject(null);

	        // Save owning side
	        projectRepository.save(project);
	}

	@Override
	public ProjectsResponseDto getActiveProjectsByEmployee(Long employeeId) {
		 Project project = empRepo.findProjectByEmployeeId(employeeId);
		 return mapper.map(project, ProjectsResponseDto.class);
	}

	@Override
	public List<ProjectsResponseDto> getProjectsByEmployeeId(Long employeeId) {
		 List<Project> projects = projectRepository.findProjectsByEmployeeId(employeeId);
		 return projects.stream().map(project -> mapper.map(project, ProjectsResponseDto.class)).toList();
	}
}
