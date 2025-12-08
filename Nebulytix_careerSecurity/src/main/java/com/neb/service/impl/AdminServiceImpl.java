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
import com.neb.dto.UpdateEmployeeResponseDto;
import com.neb.dto.WorkResponseDto;
import com.neb.dto.user.RegisterNewClientRequest;
import com.neb.dto.user.RegisterNewUerRequest;
import com.neb.dto.user.UserDto;
import com.neb.entity.Employee;
import com.neb.entity.Users;
import com.neb.entity.Work;
import com.neb.exception.CustomeException;
import com.neb.exception.HrNotFoundException;
import com.neb.repo.EmployeeRepository;
import com.neb.repo.WorkRepository;
import com.neb.service.AdminService;
import com.neb.service.UsersService;
import com.neb.util.ReportGeneratorPdf;


@Service
public class AdminServiceImpl implements AdminService{

	@Autowired
    private EmployeeRepository empRepo;
	
	@Autowired
    private WorkRepository workRepo;

    @Autowired
    private ModelMapper mapper;
    
    @Autowired
    private UsersService usersService;
    
    @Value("${task.attachment}")
    private String uploadDir;
    
    
    
    @Override
	public Long createAdmin(UserDto userReq) {
		
    	Users user = usersService.createUser(userReq);
		return user.getId();
	}

	@Override
	public Long createEmployee(RegisterNewUerRequest empReq) {
		
		Users user = usersService.createUser(empReq.getUserDto());
		
		if(user!=null) {
			
			
		}
		return null;
	}

	@Override
	public Long createClient(RegisterNewClientRequest clientReq) {
		// TODO Auto-generated method stub
		return null;
	}
    
   
    
     
//     //  ----------Get Employee List-------------
//	@Override
//	public List<EmployeeDetailsResponseDto> getEmployeeList() {
//		
//		//getting all employee list without admin
//		  List<Employee> employeeList=	empRepo.findByLoginRoleNotIn(List.of("admin","hr"));	    
//	    if(employeeList==null) {
//	    	throw new CustomeException("Employees not found");
//	    }
//	    
//	    List<EmployeeDetailsResponseDto> empListRes = employeeList.stream().map(emp->{
//	    	
//	    	EmployeeDetailsResponseDto empResDto = mapper.map(emp, EmployeeDetailsResponseDto.class);
//	    	return empResDto;
//	    }).collect(Collectors.toList());
//	    
//	    return empListRes;
//	}
//	
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
    		throw new CustomeException("works not found for employee with employee id :"+empId);
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
        dto.setEmployeeEmail(work.getEmployee().getEmail());
        dto.setAttachmentUrl(work.getAttachmentUrl());
        dto.setReportAttachmentUrl(work.getReportAttachmentUrl());
        return dto;
    }

	@Override
	public String deleteHr(Long id) {
		
		Optional<Employee> emp = empRepo.findById(id);	
		if(emp.isPresent()) {
			empRepo.deleteById(id);
			return "Hr deleted with id:"+id;
		}
		else {
			throw new CustomeException("Hr not found with id :"+id);
		}
	}

	@Override
	public EmployeeDetailsResponseDto getEmployee(Long id) {

		Employee emp = empRepo.findById(id).orElseThrow(()->new CustomeException("Employee not found wuith id :"+id));
		return mapper.map(emp, EmployeeDetailsResponseDto.class);	
	}
//	@Override
//	public Page<EmployeeDetailsResponseDto> getHrList(int page, int size, String sort) {
//
//	    Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
//
//	    Page<Employee> hrPage = empRepo.findAllHrs(pageable);
//
//	    if (hrPage.isEmpty()) {
//	        throw new HrNotFoundException("No HR records found");
//	    }
//
//	    return hrPage.map(e -> {
//	        EmployeeDetailsResponseDto dto = new EmployeeDetailsResponseDto();
//
//	        dto.setId(e.getId());
//	        dto.setFirstName(e.getFirstName());
//	        dto.setLastName(e.getLastName());
//	        dto.setEmail(e.getEmail());
//	        dto.setMobile(e.getMobile());
//	        dto.setCardNumber(e.getCardNumber());
//	        dto.setJobRole(e.getJobRole());
//	        dto.setDomain(e.getDomain());
//	        dto.setGender(e.getGender());
//	        dto.setJoiningDate(e.getJoiningDate());
//	        dto.setSalary(e.getSalary());
//	        dto.setDaysPresent(e.getDaysPresent());
//	        dto.setPaidLeaves(e.getPaidLeaves());
//	       
//	        dto.setBankAccountNumber(e.getBankAccountNumber());
//	        dto.setIfscCode(e.getIfscCode());
//	        dto.setBankName(e.getBankName());
//	        dto.setPfNumber(e.getPfNumber());
//	        dto.setPanNumber(e.getPanNumber());
//	        dto.setUanNumber(e.getUanNumber());
//	        dto.setEpsNumber(e.getEpsNumber());
//	        dto.setEsiNumber(e.getEsiNumber());
//
//	        return dto;
//	    });
//	}
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
	            .orElseThrow(() -> new CustomeException("HR not found with id: " + id));


	    // -------- BASIC DETAILS --------
	    if (updateReq.getFirstName() != null && !updateReq.getFirstName().isEmpty())
	        hr.setFirstName(updateReq.getFirstName());

	    if (updateReq.getLastName() != null && !updateReq.getLastName().isEmpty())
	        hr.setLastName(updateReq.getLastName());

	    if (updateReq.getEmail() != null && !updateReq.getEmail().isEmpty())
	        hr.setEmail(updateReq.getEmail());

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



//	// add  admin 
//	@Override
//	public AddEmployeeResponseDto addAdmin(AddEmployeeRequestDto addEmpReq) 
//	{
//		 // check if employee with same email already exists
//        if (empRepo.existsByEmail(addEmpReq.getEmail())) {
//        	throw new CustomeException("Admin with email :" + addEmpReq.getEmail() + " already exists");
//        }
//
//        // map DTO to entity
//        Employee emp = mapper.map(addEmpReq, Employee.class);
//
//        // save entity
//        Employee savedEmp = empRepo.save(emp);
//
//        // map saved entity to response DTO
//        AddEmployeeResponseDto addEmpRes = mapper.map(savedEmp, AddEmployeeResponseDto.class);
//
//        return addEmpRes;
//		
//	}

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
	
	}
