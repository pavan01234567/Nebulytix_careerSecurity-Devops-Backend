package com.neb.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.neb.constants.WorkStatus;
import com.neb.dto.AddWorkRequestDto;
import com.neb.dto.EmployeeDetailsResponseDto;
import com.neb.dto.UpdateEmployeeRequestDto;
import com.neb.dto.WorkResponseDto;
import com.neb.dto.client.ClientDto;
import com.neb.dto.client.ClientProfileDto;
import com.neb.dto.employee.EmployeeProfileDto;
import com.neb.dto.user.AdminProfileDto;
import com.neb.dto.user.RegisterNewClientRequest;
import com.neb.dto.user.RegisterNewUerRequest;
import com.neb.dto.user.UserDto;
import com.neb.entity.Client;
import com.neb.entity.Employee;
import com.neb.entity.Users;
import com.neb.entity.Work;
import com.neb.exception.CustomeException;
import com.neb.exception.ResourceNotFoundException;
import com.neb.repo.ClientRepository;
import com.neb.repo.EmployeeRepository;
import com.neb.repo.ProjectRepository;
import com.neb.repo.UsersRepository;
import com.neb.repo.WorkRepository;
import com.neb.service.AdminService;
import com.neb.service.ClientService;
import com.neb.service.EmployeeService;
import com.neb.service.UsersService;
import com.neb.util.AuthUtils;
import com.neb.util.ReportGeneratorPdf;

import jakarta.transaction.Transactional;


@Service
public class AdminServiceImpl implements AdminService{

	@Autowired
    private EmployeeRepository empRepo;
	
	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
    private WorkRepository workRepo;
	@Autowired
	private ProjectRepository projectRepo;

    @Autowired
    private ModelMapper mapper;
    
    @Autowired
    private UsersService usersService;
    
    @Autowired
    private ClientService clientService;
    
    @Autowired
    private UsersRepository usersRepository;
    
    @Value("${task.attachment}")
    private String uploadDir;
    @Autowired
    private ClientRepository  clientRepo;
    
    
    
    @Override
    @Transactional
	public Long createAdmin(UserDto userReq) {
		
    	Users user = usersService.createUser(userReq);
		return user.getId();
	}

	@Override
	@Transactional
	public Long createEmployee(RegisterNewUerRequest empReq) {
		
		Users user = usersService.createUser(empReq.getUserDto());
		
		if(user!=null) {
			
			Long employeeId = employeeService.createEmployee(empReq.getEmpReq(), user);
			if(employeeId!=null) {
				return user.getId();
			}
		}
		return null;
	}

	@Override
	@Transactional
	public Long createClient(RegisterNewClientRequest clientReq) {
		
		Users user = usersService.createUser(clientReq.getUserDto());
		
		if(user!=null) {
			Long clientId = clientService.createClient(clientReq.getClientReq(), user);
			if(clientId!=null) {
				return user.getId();
			}
		}
		
		return null;
	}
	
	@Override
	public AdminProfileDto getMyProfile() {
		
		String email = AuthUtils.getCurrentUserEmail();
        if (email == null) throw new RuntimeException("Not authenticated");

        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        AdminProfileDto dto = new AdminProfileDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setEnabled(user.isEnabled());

        return dto;
	}
    
           //............. adding work ..............
    public String assignWork(AddWorkRequestDto request,MultipartFile file) {
        Employee emp = empRepo.findById(request.getEmployeeId())
                .orElseThrow(() -> new CustomeException("Employee not found with id :"+request.getEmployeeId()));

        Work work = new Work();
        work.setTitle(request.getTitle());
        work.setDescription(request.getDescription());
        work.setAssignedDate(LocalDate.now());
        work.setDueDate(request.getDueDate());
        work.setStatus(WorkStatus.ASSIGNED);
        work.setEmployee(emp);
        
        if (file != null && !file.isEmpty()) {
            // validate PDF
            if (!"application/pdf".equals(file.getContentType())) {
                throw new CustomeException("Only PDF attachment allowed");
            }
            
            try {
            	// ensure directory exists
                Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
                Files.createDirectories(uploadPath);

                String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
                // optionally add unique suffix
                String fileName = System.currentTimeMillis() + "_" + originalFilename;
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // set URL or path in work
                work.setAttachmentUrl("/uploads/tasks/" + fileName);

            }catch (IOException ex) {
                throw new CustomeException("Could not store file. Error: " + ex.getMessage());
            }
        }
        
        Work savedWork = workRepo.save(work);
        
        if(savedWork!=null) {
        	return "Task Assigned Successfully";
        }
        else
        return "failed to assign task";
    }

    public List<WorkResponseDto> getAllWorks(Long empId) {
    	List<Work> allWork = workRepo.findByEmployeeId(empId);
    	if(allWork==null) {
    		throw new CustomeException("works not found");
    	}
        return allWork
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    	
    }

    public List<WorkResponseDto> getWorkByEmployee(Long empId) {
    	
    	List<Work> workListByEmployeeId = workRepo.findByEmployeeId(empId);
    	if(workListByEmployeeId==null) {
    		throw new ResourceNotFoundException("works not found for employee with employee id :"+empId);
    	}
        return workListByEmployeeId
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    
    }
       
    private WorkResponseDto mapToDto(Work work) {
        WorkResponseDto dto = new WorkResponseDto();
        dto.setId(work.getId());
        dto.setTitle(work.getTitle());
        dto.setDescription(work.getDescription());
        dto.setAssignedDate(work.getAssignedDate());
        dto.setDueDate(work.getDueDate());
        dto.setStatus(work.getStatus());
        dto.setReportDetails(work.getReportDetails());
        dto.setSubmittedDate(work.getSubmittedDate());
        dto.setEmployeeId(work.getEmployee().getId());
        dto.setEmployeeName(work.getEmployee().getFirstName() + " " + work.getEmployee().getLastName());
        //dto.setEmployeeEmail(work.getEmployee().getEmail());
        dto.setAttachmentUrl(work.getAttachmentUrl());
        dto.setReportAttachmentUrl(work.getReportAttachmentUrl());
        return dto;
    }

	@Override
	public String deleteHr(Long id) {
		
		Employee employee = empRepo.findById(id)
                .orElseThrow(() -> new CustomeException("Employee not found with id :"+id));

        // Soft delete employee
        employee.setEmpStatus("inactive");

        // Disable linked user
        Users user = employee.getUser();
        if (user != null) {
            user.setEnabled(false);
            usersRepository.save(user);
        }

        empRepo.save(employee);

        return "Employee and user account deactivated successfully";
    
	    
	}

	@Override
	public EmployeeDetailsResponseDto getEmployee(Long id) {

		Employee emp = empRepo.findById(id).orElseThrow(()->new ResourceNotFoundException("Employee not found wuith id :"+id));
		return mapper.map(emp, EmployeeDetailsResponseDto.class);	
	}


	@Override
	public byte[] generateDailyReport(LocalDate date) throws Exception {
	    List<Work> works = workRepo.findBySubmittedDate(date);
	    if (works == null || works.isEmpty()) {
	        // error handling
	    }
	    ReportGeneratorPdf pdfGenerator = new ReportGeneratorPdf();
	    byte[] dailyReportPDF = pdfGenerator.generateReportPDF(works, date);
	    return dailyReportPDF;
	}
	@Override
	public EmployeeDetailsResponseDto updateHrDetails(Long id, UpdateEmployeeRequestDto updateReq) {

	    Employee hr = empRepo.findById(id)
	            .orElseThrow(() -> new ResourceNotFoundException("HR not found with id: " + id));


	    // -------- BASIC DETAILS --------
	    if (updateReq.getFirstName() != null && !updateReq.getFirstName().isEmpty())
	        hr.setFirstName(updateReq.getFirstName());

	    if (updateReq.getLastName() != null && !updateReq.getLastName().isEmpty())
	        hr.setLastName(updateReq.getLastName());

//	    if (updateReq.getEmail() != null && !updateReq.getEmail().isEmpty())
//	        hr.setEmail(updateReq.getEmail());

	    if (updateReq.getMobile() != null && !updateReq.getMobile().isEmpty())
	        hr.setMobile(updateReq.getMobile());

	    if (updateReq.getCardNumber() != null && !updateReq.getCardNumber().isEmpty())
	        hr.setCardNumber(updateReq.getCardNumber());


	    if (updateReq.getGender() != null && !updateReq.getGender().isEmpty())
	        hr.setGender(updateReq.getGender());

	    // -------- SALARY + LEAVES --------
	    if (updateReq.getSalary() != null)
	        hr.setSalary(updateReq.getSalary());

	    if (updateReq.getPaidLeaves() != 0)
	        hr.setPaidLeaves(updateReq.getPaidLeaves());

	    // -------- BANK & TAX DETAILS --------
//	    if (updateReq.getBankAccountNumber() != null && !updateReq.getBankAccountNumber().isEmpty())
//	        hr.setBankAccountNumber(updateReq.getBankAccountNumber());
//
//	    if (updateReq.getIfscCode() != null && !updateReq.getIfscCode().isEmpty())
//	        hr.setIfscCode(updateReq.getIfscCode());
//
//	    if (updateReq.getBankName() != null && !updateReq.getBankName().isEmpty())
//	        hr.setBankName(updateReq.getBankName());
//
//	    if (updateReq.getPfNumber() != null && !updateReq.getPfNumber().isEmpty())
//	        hr.setPfNumber(updateReq.getPfNumber());
//
//	    if (updateReq.getPanNumber() != null && !updateReq.getPanNumber().isEmpty())
//	        hr.setPanNumber(updateReq.getPanNumber());
//
//	    if (updateReq.getUanNumber() != null && !updateReq.getUanNumber().isEmpty())
//	        hr.setUanNumber(updateReq.getUanNumber());
//
//	    if (updateReq.getEpsNumber() != null && !updateReq.getEpsNumber().isEmpty())
//	        hr.setEpsNumber(updateReq.getEpsNumber());
//
//	    if (updateReq.getEsiNumber() != null && !updateReq.getEsiNumber().isEmpty())
//	        hr.setEsiNumber(updateReq.getEsiNumber());

	    // -------- SAVE --------
	    Employee updatedHr = empRepo.save(hr);

	    return mapper.map(updatedHr, EmployeeDetailsResponseDto.class);
	}

     // delete the admin based on ID
	@Override
	public String deleteAdmin(Long id)
	{

		Optional<Employee> emp = empRepo.findById(id);	
		if(emp.isPresent()) {
			empRepo.deleteById(id);
			return "Admin deleted with id:"+id;
		}
		else {
			throw new CustomeException("Admin not found with id :"+id);
		}
	}

	@Override
	public List<AdminProfileDto> getOnlyAdmin() 
	{
		  List<Users> admins = empRepo.findOnlyAdmin();
		  System.out.println(admins);
		 List<AdminProfileDto> allAdmin = admins.stream()
			        .map(emp -> mapper.map(emp,AdminProfileDto.class))
			        .collect(Collectors.toList());
         System.out.println(allAdmin);
		 return allAdmin;
	}
	
	
	@Override
	public List<ClientDto> getClientList() {
	    List<Client> clients = clientRepo.findAll();

	    return clients.stream().map(c -> {
	        ClientDto dto = new ClientDto();
	        dto.setId(c.getId());
	        dto.setCompanyName(c.getCompanyName());
	        dto.setContactPerson(c.getContactPerson());
	        dto.setContactEmail(c.getContactEmail());
	        dto.setPhone(c.getPhone());
	        dto.setAlternatePhone(c.getAlternatePhone());
	        dto.setAddress(c.getAddress());
	        dto.setWebsite(c.getWebsite());
	        dto.setIndustryType(c.getIndustryType());
	        dto.setGstNumber(c.getGstNumber());
	        dto.setStatus(c.getStatus());
	        dto.setCreatedDate(c.getCreatedDate());
	        dto.setUpdatedDate(c.getUpdatedDate());
	        return dto;
	    }).collect(Collectors.toList());
	}

	@Override
	public List<EmployeeProfileDto> getOnlyHr() {
		 List<Employee> employees = empRepo.findOnlyHr();
		 
		 return employees.stream()
			        .map(emp -> {
			            EmployeeProfileDto dto = new EmployeeProfileDto();
			            dto.setId(emp.getId());
			            dto.setFirstName(emp.getFirstName());
			            dto.setLastName(emp.getLastName());
			            dto.setDesignation(emp.getDesignation());
			            dto.setDepartment(emp.getDepartment());
			            dto.setGender(emp.getGender());
			            dto.setJoiningDate(emp.getJoiningDate());
			            dto.setSalary(emp.getSalary());
			            dto.setProfilePictureUrl(emp.getProfilePictureUrl());
			            dto.setMobile(emp.getMobile());

			            // From User entity
			            if (emp.getUser() != null) {
			                dto.setEmail(emp.getUser().getEmail());
			            }

			            return dto;
			        })
			        .collect(Collectors.toList());

	}

	@Override
	public List<EmployeeProfileDto> getOnlyEmployee() {
		 List<Employee> allEmployees = empRepo.findOnlyEmployees();
		 return allEmployees.stream()
			        .map(emp -> {
			            EmployeeProfileDto dto = new EmployeeProfileDto();
			            dto.setId(emp.getId());
			            dto.setFirstName(emp.getFirstName());
			            dto.setLastName(emp.getLastName());
			            dto.setDesignation(emp.getDesignation());
			            dto.setDepartment(emp.getDepartment());
			            dto.setGender(emp.getGender());
			            dto.setJoiningDate(emp.getJoiningDate());
			            dto.setSalary(emp.getSalary());
			            dto.setProfilePictureUrl(emp.getProfilePictureUrl());
			            dto.setMobile(emp.getMobile());

			            // From User entity
			            if (emp.getUser() != null) {
			                dto.setEmail(emp.getUser().getEmail());
			            }

			            return dto;
			        })
			        .collect(Collectors.toList());
	}

	@Override
	public List<EmployeeProfileDto> getOnlyManager() 
	{ 
		List<Employee> employees = empRepo.findOnlyManager();
	 
	 return employees.stream()
		        .map(emp -> {
		            EmployeeProfileDto dto = new EmployeeProfileDto();
		            dto.setId(emp.getId());
		            dto.setFirstName(emp.getFirstName());
		            dto.setLastName(emp.getLastName());
		            dto.setDesignation(emp.getDesignation());
		            dto.setDepartment(emp.getDepartment());
		            dto.setGender(emp.getGender());
		            dto.setJoiningDate(emp.getJoiningDate());
		            dto.setSalary(emp.getSalary());
		            dto.setProfilePictureUrl(emp.getProfilePictureUrl());

		            // If mobile is in Employee
		            dto.setMobile(emp.getMobile());

		            // From User entity
		            if (emp.getUser() != null) {
		                dto.setEmail(emp.getUser().getEmail());
		            }

		            return dto;
		        })
		        .collect(Collectors.toList());
		
	}

	@Override
	public List<ClientProfileDto> getOnlyClient() 
	{
		List<Client> clients = empRepo.findOnlyClients();

	    return clients.stream()
	        .map(client -> {
	            ClientProfileDto dto = new ClientProfileDto();
	            dto.setId(client.getId());
	            dto.setCompanyName(client.getCompanyName());
	            dto.setContactPerson(client.getContactPerson());
	            dto.setContactEmail(client.getContactEmail());
	            dto.setPhone(client.getPhone());
	            dto.setAlternatePhone(client.getAlternatePhone());
	            dto.setAddress(client.getAddress());
	            dto.setWebsite(client.getWebsite());
	            dto.setIndustryType(client.getIndustryType());
	            dto.setGstNumber(client.getGstNumber());

	            return dto;
	        })
	        .collect(Collectors.toList());
     
	}

	
}
