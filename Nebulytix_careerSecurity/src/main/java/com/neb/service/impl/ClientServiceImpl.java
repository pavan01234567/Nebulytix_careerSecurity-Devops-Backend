package com.neb.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.neb.constants.WorkStatus;
import com.neb.dto.AddWorkRequestDto;
import com.neb.dto.EmployeeResponseDto;
import com.neb.dto.WorkResponseDto;
import com.neb.dto.client.AddClientRequest;
import com.neb.dto.client.ClientProfileDto;
import com.neb.dto.employee.EmployeeProfileDto;
import com.neb.entity.Client;
import com.neb.entity.DailyReport;
import com.neb.entity.Employee;
import com.neb.entity.Project;
import com.neb.entity.Users;
import com.neb.entity.Work;
import com.neb.exception.CustomeException;
import com.neb.repo.ClientRepository;
import com.neb.repo.DailyReportRepository;
import com.neb.repo.EmployeeRepository;
import com.neb.repo.ProjectRepository;
import com.neb.repo.UsersRepository;
import com.neb.repo.WorkRepository;
import com.neb.service.ClientService;
import com.neb.util.AuthUtils;

@Service
public class ClientServiceImpl implements ClientService{

	@Autowired
	private UsersRepository usersRepository;
	@Autowired
	private ClientRepository clientRepository;
    @Autowired
	private ModelMapper mapper;
	@Autowired	
    private EmployeeRepository employeeRepo;
	@Autowired
	private  ProjectRepository projectRepo;
	@Autowired
	private DailyReportRepository dailyReportRepository;
    @Autowired
	private WorkRepository workRepo;
	
	
	@Override
	public ClientProfileDto getMyProfile() {

        String email = AuthUtils.getCurrentUserEmail();
        if (email == null) throw new RuntimeException("User not authenticated");

        Users user = usersRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        Client client = clientRepository.findByUserId(user.getId()).orElseThrow(() -> new RuntimeException("Client profile not found"));
        ClientProfileDto clientProfileDto = mapper.map(client, ClientProfileDto.class);
      return clientProfileDto;
    }

     @Override
	public Long createClient(AddClientRequest addClientReq, Users user) {
		
		Client client = mapper.map(addClientReq, Client.class);
		client.setUser(user);
		Client savedClient = clientRepository.save(client);
		
		return savedClient.getId();
	}
	  // Helper: Get logged-in client
    private Users getLoggedInClient() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usersRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Client not found with email: " + email));
    }

      @Override
    public List<Project> getProjectsForLoggedInClient() {
        Users client = getLoggedInClient();
        return projectRepo.findByClient_Id(client.getId());
    }

    @Override
    public String getProjectStatus(Long projectId) {
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));
        return project.getStatus();
    }

    @Override
    public List<EmployeeProfileDto> getEmployeesByProject(Long projectId) {
    	List<Employee> employees = employeeRepo.findEmployeesByProjectId(projectId);

        return employees.stream()
        		.map(emp -> {
                    EmployeeProfileDto dto = mapper.map(emp, EmployeeProfileDto.class);
                    dto.setEmail(emp.getUser().getEmail()); 
                    return dto;
                })
                .toList();
    }

    @Override
    public WorkResponseDto assignWorkToEmployee(Long projectId, AddWorkRequestDto dto) {
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));

        Employee employee = employeeRepo.findById(dto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + dto.getEmployeeId()));

        Work work = new Work();
        work.setTitle(dto.getTitle());
        work.setDescription(dto.getDescription());
        work.setDueDate(dto.getDueDate());
        work.setAssignedDate(LocalDate.now());
        work.setStatus(WorkStatus.ASSIGNED);
        work.setProject(project);
        work.setEmployee(employee);

        workRepo.save(work);

        return WorkResponseDto.fromEntity(work);
    }

    @Override
    public List<WorkResponseDto> getWorkByProject(Long projectId) {
        Optional<Work> works = workRepo.findById(projectId);
        return works.stream().map(WorkResponseDto::fromEntity).collect(Collectors.toList());
    }
    @Override
    public List<DailyReport> getReportsByProject(Long projectId) {

        // Ensure client owns this project
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new CustomeException("Project not found"));

        if (!project.getClient().getId().equals(getLoggedInClient())) {
            throw new CustomeException("You are not authorized to view this project's reports");
        }

        return dailyReportRepository.findByEmployee_Project_Id(projectId);
    }

	
}
