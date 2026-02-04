package com.neb.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
import com.neb.exception.ResourceNotFoundException;
import com.neb.repo.ClientRepository;
import com.neb.repo.EmployeeRepository;
import com.neb.repo.ProjectRepository;
import com.neb.service.ClientService;
import com.neb.service.CloudinaryService;
import com.neb.service.ProjectService;
import com.neb.util.ProjectStatus;

import jakarta.transaction.Transactional;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private EmployeeRepository empRepo;

    @Autowired
    private ClientService clientService;

    @Autowired
    private CloudinaryService cloudinaryService; 

    @Autowired
    private ModelMapper mapper;

    @Value("${cloudinary.folder.projects}")
    private String projectFolder; // 🔹 ADDED

    // =====================================================
    // GET ALL PROJECTS
    // =====================================================
    @Override
    public ResponseMessage<List<ProjectsResponseDto>> getAllProjects() {

        List<Project> projects = projectRepository.findAll();

        List<ProjectsResponseDto> dtoList = projects.stream().map(project -> {

            ProjectsResponseDto dto = mapper.map(project, ProjectsResponseDto.class);

            if (project.getClient() != null) {
                Client client = project.getClient();
                ClientProfileDto clientDto = mapper.map(client, ClientProfileDto.class);
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
        }).toList();

        return new ResponseMessage<>(200, "SUCCESS", "All projects", dtoList);
    }

   
    @Override
    public ResponseMessage<ProjectsResponseDto> getProjectById(Long id) {

        Project project = projectRepository.findProjectWithClientAndEmployees(id)
                .orElseThrow(() -> new CustomeException("Project not found"));

        ProjectsResponseDto dto = mapper.map(project, ProjectsResponseDto.class);

        if (project.getClient() != null) {
            ClientProfileDto clientDto = mapper.map(project.getClient(), ClientProfileDto.class);
            clientDto.setUserEnabled(project.getClient().getUser().isEnabled());
            dto.setClient(clientDto);
        }

        if (project.getEmployees() != null && !project.getEmployees().isEmpty()) {
            dto.setEmployees(clientService.getEmployeesByProject(id));
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

        return new ResponseMessage<>(200, "SUCCESS", "Project updated",
                ProjectResponseDto.fromEntity(project));
    }


    @Override
    public ResponseMessage<String> deleteProject(Long id) {
        projectRepository.deleteById(id);
        return new ResponseMessage<>(200, "SUCCESS", "Project deleted", null);
    }

  
    @Override
    @Transactional
    public ProjectResponseDto updateProjectStatus(Long projectId, ProjectStatus status) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomeException(
                        "Project not found with id: " + projectId));

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
            MultipartFile requirement,
            MultipartFile contract) {

        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Client not found with id " + dto.getClientId()));

        // ✅ CREATE NEW ENTITY MANUALLY
        Project project = new Project();

        // ✅ SET FIELDS EXPLICITLY (NO ID)
        project.setProjectName(dto.getProjectName());
        project.setProjectCode(dto.getProjectCode());
        project.setProjectType(dto.getProjectType());
        project.setDescription(dto.getDescription());
        project.setPriority(dto.getPriority());
        project.setBudget(dto.getBudget());
        project.setRiskLevel(dto.getRiskLevel());
        project.setTags(dto.getTags());

        project.setClient(client);
        project.setStatus(ProjectStatus.PLANNED);
        project.setProgress(0);
        project.setCreatedDate(LocalDate.now());

     
        if (quotation != null && !quotation.isEmpty()) {
            String url = cloudinaryService.uploadFile(
                    quotation, projectFolder + "/quotation", "auto");
            project.setQuotationPdfUrl(url);
        }

        if (requirement != null && !requirement.isEmpty()) {
            String url = cloudinaryService.uploadFile(
                    requirement, projectFolder + "/requirement", "auto");
            project.setRequirementDocUrl(url);
        }

        if (contract != null && !contract.isEmpty()) {
            String url = cloudinaryService.uploadFile(
                    contract, projectFolder + "/contract", "auto");
            project.setContractPdfUrl(url);
        }

        // 
        return projectRepository.save(project);
    }


    // =====================================================
    // ADD EMPLOYEE TO PROJECT
    // =====================================================
    @Override
    public ProjectResponseDto addEmployeeToProject(Long projectId, Long employeeId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomeException("Project not found"));

        Employee employee = empRepo.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));

        if (project.getEmployees().contains(employee)) {
            throw new CustomeException("Employee already assigned to this project");
        }

        project.getEmployees().add(employee);
        employee.getAssignedProjects().add(project);
        employee.setProject(project);

        return mapper.map(projectRepository.save(project), ProjectResponseDto.class);
    }

    
    @Override
    public void removeEmployeeFromProject(Long projectId, Long employeeId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        Employee employee = empRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (!project.getEmployees().contains(employee)) {
            throw new RuntimeException("Employee is not assigned to this project");
        }

        project.getEmployees().remove(employee);
        employee.getAssignedProjects().remove(project);
        employee.setProject(null);

        projectRepository.save(project);
    }

   
    @Override
    public ProjectsResponseDto getActiveProjectsByEmployee(Long employeeId) {
        Project project = empRepo.findProjectByEmployeeId(employeeId);
        return mapper.map(project, ProjectsResponseDto.class);
    }

   
    @Override
    public List<ProjectsResponseDto> getProjectsByEmployeeId(Long employeeId) {
        return projectRepository.findProjectsByEmployeeId(employeeId)
                .stream()
                .map(p -> mapper.map(p, ProjectsResponseDto.class))
                .toList();
    }
}
