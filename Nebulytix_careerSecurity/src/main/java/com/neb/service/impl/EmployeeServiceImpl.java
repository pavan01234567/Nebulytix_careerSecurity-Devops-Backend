package com.neb.service.impl;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import java.util.stream.Collectors;import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.neb.constants.WorkStatus;
import com.neb.dto.AddDailyReportRequestDto;
import com.neb.dto.EmployeeDetailsResponseDto;
import com.neb.dto.WorkResponseDto;
import com.neb.dto.employee.AddEmployeeRequest;
import com.neb.dto.employee.EmployeeProfileDto;
import com.neb.entity.DailyReport;
import com.neb.entity.Employee;
import com.neb.entity.Payslip;
import com.neb.entity.Users;
import com.neb.entity.Work;
import com.neb.exception.CustomeException;
import com.neb.repo.DailyReportRepository;
import com.neb.repo.EmployeeRepository;
import com.neb.repo.PayslipRepository;
import com.neb.repo.UsersRepository;
import com.neb.repo.WorkRepository;
import com.neb.service.EmployeeService;
import com.neb.util.AuthUtils;
import com.neb.util.PdfGeneratorUtil;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private PayslipRepository payslipRepo;

    @Autowired
    private ModelMapper mapper;
    
    @Autowired
    private WorkRepository workRepository;
    
    @Autowired
    private DailyReportRepository dailyReportRepository;
    
    @Autowired
    private UsersRepository usersRepository;

    
    @Value("${payslip.base-folder}")
    private String baseFolder;
    
    @Value("${task.attachment}")
    private String attachmentFolder;
    
    @Value("${profile.picture.folder}")
    private String baseProfileFolder;
    
    
    @Override
	public Long createEmployee(AddEmployeeRequest empReq, Users user) {
    	Employee employee = mapper.map(empReq, Employee.class);
    	employee.setUser(user);
    	System.out.println(employee);
    	Employee saveEmployee = employeeRepository.save(employee);
		return saveEmployee.getId();
	}
    


	@Override
	public EmployeeProfileDto getMyProfile() {
		
		// 1. Get logged-in user email
        String email = AuthUtils.getCurrentUserEmail();
        if (email == null) throw new RuntimeException("User not authenticated");

        // 2. Fetch user entity
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3. Fetch employee profile
        Employee emp = employeeRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Employee profile not found"));
        
        // 4. Map to DTO
        EmployeeProfileDto empDetailsDto = mapper.map(emp, EmployeeProfileDto.class);
        empDetailsDto.setEmail(user.getEmail());
        
        return empDetailsDto;
	}
    
    //Getting employee By ID
    @Override
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id).orElseThrow(() -> new CustomeException("Employee not found with id: "+id));
    }
   
	@Override
	public Payslip generatePayslip(Long employeeId, String monthYear) throws Exception{
		
		
		Employee emp = employeeRepository.findById(employeeId)
	            .orElseThrow(() -> new CustomeException("Employee not found with id: "+employeeId));
		
		Payslip p = new Payslip();
        p.setEmployee(emp);
        p.setPayslipMonth(monthYear);
        p.setGeneratedDate(LocalDateTime.now());
        p.setLocation("FLAT NO 501B,PSR PRIME TOWERS,BESIDE DLF,GACHIBOWLI,500032");

        // Salary Calculations
        double salary = emp.getSalary();
        p.setBasic(salary * 0.53);
        p.setHra(salary * 0.20);
        p.setFlexi(salary * 0.27);
        double gross = p.getBasic() + p.getHra() + p.getFlexi();//
        p.setGrossSalary(gross);
        
        // Deductions
        p.setPfDeduction(p.getBasic() * 0.12);
        p.setProfTaxDeduction(200.0);
        double ded = p.getPfDeduction() + p.getProfTaxDeduction();
        p.setTotalDeductions(ded);
        
        // Net Salary Calculation
        double net = gross - ded;
        p.setNetSalary(net);
        p.setBalance(gross);
        p.setAggrgDeduction(ded);
        p.setIncHdSalary(net);
        p.setTaxCredit(net*0.05);//random values added
     
        // Save payslip record
        p = payslipRepo.save(p);
        
        // PDF File Generation
        String fileName = emp.getCardNumber() + "_payslip" + monthYear.replace(" ", "_") + ".pdf";
        String folderPath = baseFolder + "/" + monthYear.replace(" ", "_");
        Files.createDirectories(Paths.get(folderPath));
        String fullPath = folderPath + "/" + fileName;

        
        byte[] pdfBytes = PdfGeneratorUtil.createPayslipPdf(emp, p);
        Files.write(Paths.get(fullPath), pdfBytes);

        p.setPdfPath(fullPath);
        p.setFileName(fileName);
        payslipRepo.save(p);

        return p;
	}
	 // Get employee details by EMAIL
//    public EmployeeDetailsResponseDto getEmployeeByEmail(String email) {
//    	System.out.println(email);
//    	Employee emp = employeeRepository.findByEmail(email).orElseThrow(()->new CustomeException("Employee not found with email id :"+email));
//    	EmployeeDetailsResponseDto empdetailsDto = mapper.map(emp, EmployeeDetailsResponseDto.class);
//        return empdetailsDto;
//    }
  
    public List<Work> getTasksByEmployee(Long employeeId) {
        Employee emp = getEmployeeById(employeeId);
        List<Work> workListbyEmployee = workRepository.findByEmployee(emp);
        if(workListbyEmployee==null) {
        	throw new CustomeException("work list is empty for employee with id: "+emp.getId());
        }
        return workListbyEmployee;
    }
    
    @Override
    public WorkResponseDto submitReport(Long taskId, String statusStr, String reportDetails, MultipartFile reportAttachment, LocalDate submittedDate) {
        Work task = workRepository.findById(taskId)
                .orElseThrow(() -> new CustomeException("Task not found with taskId: " + taskId));

        task.setReportDetails(reportDetails);
        task.setSubmittedDate(submittedDate);
        task.setStatus(WorkStatus.valueOf(statusStr));

        // Handle file upload
        if (reportAttachment != null && !reportAttachment.isEmpty()) {
            try {
                String fileName = System.currentTimeMillis() + "_" + reportAttachment.getOriginalFilename();
                Path uploadPath = Paths.get(attachmentFolder);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(reportAttachment.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Save relative URL for frontend access
                String fileUrl = "/uploads/tasks/" + fileName;
                task.setReportAttachmentUrl(fileUrl);
            } catch (IOException e) {
                throw new CustomeException("Failed to save attachment: " + e.getMessage());
            }
        }

        Work savedWork = workRepository.save(task);
        WorkResponseDto workRes = new WorkResponseDto();
        
        workRes.setId(savedWork.getId());
        workRes.setTitle(savedWork.getTitle());
        workRes.setAssignedDate(savedWork.getAssignedDate());
        workRes.setDueDate(savedWork.getDueDate());
        workRes.setStatus(savedWork.getStatus());
        workRes.setReportDetails(savedWork.getReportDetails());
        workRes.setSubmittedDate(savedWork.getSubmittedDate());
        workRes.setReportAttachmentUrl(savedWork.getReportAttachmentUrl());
        workRes.setAttachmentUrl(savedWork.getAttachmentUrl());
        workRes.setEmployeeId(savedWork.getEmployee().getId());
        workRes.setEmployeeName(savedWork.getEmployee().getFirstName());
       // workRes.setEmployeeEmail(savedWork.getEmployee().getEmail());
        
        return workRes ;
    }

	@Override
	public String submitDailyReport(AddDailyReportRequestDto request) {
	
		//Employee emp = empRepo.findById(request.getEmployee_id()).orElseThrow(()->new CustomeException("employee not found with id:"+request.getEmployee_id()));
		Employee emp = employeeRepository.findById(request.getEmployee_id())
	            .orElseThrow(() -> new CustomeException("employee not found with id: " + request.getEmployee_id()));

	        LocalDate date = request.getReportDate();
	        if (date == null) {
	            throw new CustomeException("reportDate must not be null");
	        }
	        
	        Optional<DailyReport> existingOpt = dailyReportRepository.findByEmployeeIdAndReportDate(emp.getId(), date);
		
	        
	         DailyReport report;
        if (existingOpt.isPresent()) {
            // update existing
            report = existingOpt.get();
            report.setSummary(request.getSummary());
            
        } else {
            // create new
            report = new DailyReport();
            report.setEmployee(emp);
            report.setReportDate(date);
            report.setSummary(request.getSummary());
        }

        DailyReport saved = dailyReportRepository.save(report);

        if (saved != null && saved.getId() != null) {
            return existingOpt.isPresent() ? "Report updated successfully!" : "Report submitted successfully!";
        } else {
            return "failed to submit report";
        }
    }
	
	@Override
	public String uploadProfilePicture(Long employeeId, MultipartFile file) {
	    Employee emp = employeeRepository.findById(employeeId)
	        .orElseThrow(() -> new CustomeException("employee not found with id: " + employeeId));

	    if (file == null || file.isEmpty()) {
	        throw new CustomeException("No file provided");
	    }

	    // Validate content type (image) - basic check
	    String contentType = file.getContentType();
	    if (contentType == null || !contentType.startsWith("image/")) {
	        throw new CustomeException("Only image files are allowed");
	    }

	    //check size (e.g., <= 5MB)
	    long maxSize = 5 * 1024 * 1024;
	    if (file.getSize() > maxSize) {
	        throw new CustomeException("File too large. Max allowed is 5 MB");
	    }

	    // Build filename - use UUID to avoid name collisions
	    String original = file.getOriginalFilename();
	    String ext = "";
	    if (original != null && original.contains(".")) {
	        ext = original.substring(original.lastIndexOf('.'));
	    }
	    String filename = java.util.UUID.randomUUID().toString() + ext;

	    try {
	        // base folder from property
	        Path uploadDir = Paths.get(baseProfileFolder);
	        if (!Files.exists(uploadDir)) {
	            Files.createDirectories(uploadDir);
	        }

	        Path target = uploadDir.resolve(filename);
	        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

	        // public URL served by resource handler
	        String publicUrl = "/uploads/profiles/" + filename;

	        // update employee entity
	        emp.setProfilePictureUrl(publicUrl);
	        emp.setProfilePicturePath(target.toAbsolutePath().toString());
	        employeeRepository.save(emp);

	        return publicUrl;
	    } catch (IOException e) {
	        throw new CustomeException("Failed to save profile picture: " + e.getMessage());
	    }
	}	
	
	@Override
	public boolean deleteProfilePicture(Long employeeId) {

	    Employee emp = employeeRepository.findById(employeeId)
	            .orElseThrow(() -> new RuntimeException("Employee not found"));

	    String imagePath = emp.getProfilePicturePath(); // FULL path (absolute path)

	    if (imagePath == null || imagePath.isEmpty()) {
	        return false; // nothing to delete
	    }

	    File file = new File(imagePath);

	    // delete file physically from folder
	    if (file.exists()) {
	        boolean deleted = file.delete();
	        System.out.println("File deleted: " + deleted);
	    }

	    // remove from DB
	    emp.setProfilePicturePath(null);
	    emp.setProfilePictureUrl(null); // if you use URLs
	    employeeRepository.save(emp);

	    return true;
	}

	
}
	       
