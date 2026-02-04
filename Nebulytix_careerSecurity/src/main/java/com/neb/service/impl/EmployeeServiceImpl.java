package com.neb.service.impl;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.neb.constants.WorkStatus;
import com.neb.dto.AddDailyReportRequestDto;
import com.neb.dto.EmployeeDTO;
import com.neb.dto.EmployeeLeaveDTO;
import com.neb.dto.EmployeeRegulationDTO;
import com.neb.dto.WorkResponseDto;
import com.neb.dto.employee.AddEmployeeRequest;
import com.neb.dto.employee.EmployeeProfileDto;
import com.neb.dto.employee.UpdateEmployeeRequestDto;
import com.neb.dto.employee.UpdateEmployeeResponseDto;
import com.neb.entity.DailyReport;
import com.neb.entity.Employee;
import com.neb.entity.EmployeeLeaveBalance;
import com.neb.entity.EmployeeLeaves;
import com.neb.entity.EmployeeLogInDetails;
import com.neb.entity.MisPunchRequest;
import com.neb.entity.Payslip;
import com.neb.entity.Users;
import com.neb.entity.Work;
import com.neb.exception.AlreadyCheckedOutException;
import com.neb.exception.CustomeException;
import com.neb.exception.EmployeeAlreadyLoggedInException;
import com.neb.exception.EmployeeNotFoundException;
import com.neb.exception.EmployeeNotLoggedInException;
import com.neb.exception.InsufficientLeaveBalanceException;
import com.neb.exception.InvalidDateRangeException;
import com.neb.repo.DailyReportRepository;
import com.neb.repo.EmployeeLeaveBalanceRepo;
import com.neb.repo.EmployeeLeaveRepository;
import com.neb.repo.EmployeeLoginDetailsRepo;
import com.neb.repo.EmployeeRepository;
import com.neb.repo.MisPunchRequestRepo;
import com.neb.repo.PayslipRepository;
import com.neb.repo.UsersRepository;
import com.neb.repo.WorkRepository;
import com.neb.service.CloudinaryService;
import com.neb.service.EmployeeService;
import com.neb.service.NotificationService;
import com.neb.util.ApprovalStatus;
import com.neb.util.AuthUtils;
import com.neb.util.EmployeeDayStatus;
import com.neb.util.EmployeeLeaveType;
import com.neb.util.PdfGeneratorUtil;
import com.neb.util.ReportGeneratorPdf;

import jakarta.transaction.Transactional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private PayslipRepository payslipRepo;
    @Autowired
	private EmployeeLoginDetailsRepo empLoginRepo;
    @Autowired
    private ModelMapper mapper;
    @Autowired
	private EmployeeLeaveRepository empLeaveRepo;

	@Autowired
	private NotificationService notificationService;
	@Autowired
private MisPunchRequestRepo MisPunchRequestRepo;
	
	
    @Autowired
	private EmployeeLeaveBalanceRepo leaveBalanceRepo;
    @Autowired
    private WorkRepository workRepository;
    @Autowired
    private DailyReportRepository dailyReportRepository;
    @Autowired
    private UsersRepository usersRepository;
   
    @Autowired
    private CloudinaryService cloudinaryService;
    
    @Autowired
    private ReportGeneratorPdf ReportGeneratorPdf;


    @Value("${cloudinary.folder.profile-pictures}")
    private String profileFolder;

    @Value("${cloudinary.folder.payslips}") 
    private String payslipFolder;
    
    
    
    @Override
    public Long createEmployee(AddEmployeeRequest empReq, Users user) {
        Employee employee = mapper.map(empReq, Employee.class);
        employee.setUser(user);
        return employeeRepository.save(employee).getId();
    }

    @Override
    public EmployeeProfileDto getMyProfile() {
        String email = AuthUtils.getCurrentUserEmail();
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Employee emp = employeeRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Employee profile not found"));

        EmployeeProfileDto dto = mapper.map(emp, EmployeeProfileDto.class);
        dto.setEmail(user.getEmail());
        return dto;
    }
    
    //Getting employee By ID
    @Override
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id).orElseThrow(() -> new CustomeException("Employee not found with id: "+id));
    }
  
    @Override
    public Payslip generatePayslip(Long employeeId, String monthYear) throws Exception {
        Employee emp = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new CustomeException("Employee not found with id: " + employeeId));

        Payslip p = new Payslip();
        p.setEmployee(emp);
        p.setPayslipMonth(monthYear);
        p.setGeneratedDate(LocalDateTime.now());
        p.setLocation("FLAT NO 501B, PSR PRIME TOWERS, GACHIBOWLI, 500032");

        // ===== Salary Calculations =====
        double salary = emp.getSalary();
        p.setBasic(salary * 0.53);
        p.setHra(salary * 0.20);
        p.setFlexi(salary * 0.27);

        double gross = p.getBasic() + p.getHra() + p.getFlexi();
        p.setGrossSalary(gross);

        p.setPfDeduction(p.getBasic() * 0.12);
        p.setProfTaxDeduction(200.0);

        double deductions = p.getPfDeduction() + p.getProfTaxDeduction();
        p.setTotalDeductions(deductions);

        double net = gross - deductions;
        p.setBalance(gross);
        p.setAggrgDeduction(deductions);
        p.setIncHdSalary(net);
        p.setTaxCredit(net * 0.05);

        // Save initial payslip
        p = payslipRepo.save(p);

        // ===== Generate PDF =====
        byte[] pdfBytes = PdfGeneratorUtil.createPayslipPdf(emp, p);
        String fileName = emp.getCardNumber() + "_payslip_" + monthYear.replace(" ", "_");

        // ===== Upload PDF to Cloudinary via service =====
        String pdfUrl = cloudinaryService.uploadFile(
                pdfBytes,
                fileName,
                payslipFolder + "/" + monthYear.replace(" ", "_"),
                "raw"
        );

        // Save Cloudinary info
        p.setPdfPath(pdfUrl); // ✅ MODIFIED: Store secure URL instead of public_id
        p.setFileName(fileName);

        payslipRepo.save(p);

        return p;
    }

	
    public List<Work> getTasksByEmployee(Long employeeId) {
        Employee emp = getEmployeeById(employeeId);
        List<Work> workListbyEmployee = workRepository.findByEmployee(emp);
        if(workListbyEmployee==null) {
        	throw new CustomeException("work list is empty for employee with id: "+emp.getId());
        }
        return workListbyEmployee;
    }
    
    @Override
    public WorkResponseDto submitReport(Long taskId, String statusStr, String reportDetails,
                                       MultipartFile reportAttachment, LocalDate submittedDate) {
        Work task = workRepository.findById(taskId)
                .orElseThrow(() -> new CustomeException("Task not found"));

        task.setReportDetails(reportDetails);
        task.setSubmittedDate(submittedDate);
        task.setStatus(WorkStatus.valueOf(statusStr));

        // ✅ Upload attachment to Cloudinary
        if (reportAttachment != null && !reportAttachment.isEmpty()) {
            String fileUrl = cloudinaryService.uploadFile(reportAttachment, "task-reports", "auto");
            task.setReportAttachmentUrl(fileUrl);
        }


        Work saved = workRepository.save(task);

        WorkResponseDto dto = new WorkResponseDto();
        dto.setId(saved.getId());
        dto.setTitle(saved.getTitle());
        dto.setAssignedDate(saved.getAssignedDate());
        dto.setDueDate(saved.getDueDate());
        dto.setStatus(saved.getStatus());
        dto.setReportDetails(saved.getReportDetails());
        dto.setSubmittedDate(saved.getSubmittedDate());
        dto.setReportAttachmentUrl(saved.getReportAttachmentUrl());
        dto.setAttachmentUrl(saved.getAttachmentUrl());
        dto.setEmployeeId(saved.getEmployee().getId());
        dto.setEmployeeName(saved.getEmployee().getFirstName());

        return dto;
    }

    @Transactional
    @Override
    public String submitDailyReport(AddDailyReportRequestDto request) {

        Employee emp = employeeRepository.findById(request.getEmployee_id())
                .orElseThrow(() ->
                        new CustomeException("Employee not found"));

        LocalDate reportDate = request.getReportDate();
        if (reportDate == null) {
            throw new CustomeException("Report date must not be null");
        }

        Optional<DailyReport> existingOpt =
                dailyReportRepository.findByEmployeeIdAndReportDate(
                        emp.getId(), reportDate);

        DailyReport report;
        if (existingOpt.isPresent()) {
            report = existingOpt.get();
            report.setSummary(request.getSummary());
        } else {
            report = new DailyReport();
            report.setEmployee(emp);
            report.setReportDate(reportDate);
            report.setSummary(request.getSummary());
        }

        dailyReportRepository.save(report);

        // ================= GENERATE PDF =================
        try {
            List<DailyReport> reports =
                    dailyReportRepository.findAllByReportDate(reportDate);

            byte[] pdfBytes =
                    ReportGeneratorPdf.generateDailyReportForEmployees(
                            reports, reportDate);

            String fileName = "daily_report_" + reportDate;

            String pdfUrl = cloudinaryService.uploadFile(
                    pdfBytes,
                    fileName,
                    "daily-reports/" + reportDate,
                    "raw"
            );

            // ✅ Store Cloudinary URL
            report.setDailyReportUrl(pdfUrl);
            dailyReportRepository.save(report);

        } catch (Exception e) {
            throw new CustomeException(
                    "Failed to generate/upload daily report PDF: " + e.getMessage());
        }

        return existingOpt.isPresent()
                ? "Daily report updated & uploaded to Cloudinary"
                : "Daily report submitted & uploaded to Cloudinary";
    }

	@Override
	public String saveProfilePictureUrl(Long employeeId, String imageUrl) {
	    
	    Employee emp = employeeRepository.findById(employeeId)
	            .orElseThrow(() -> new CustomeException("Employee not found"));

	    try {
	       
	        emp.setProfilePictureUrl(imageUrl);
	        emp.setProfilePicturePath(imageUrl); 

	        employeeRepository.save(emp);
	        return imageUrl;
	    } catch (Exception e) {
	        throw new CustomeException("Database update failed: " + e.getMessage());
	    }
	}
	@Override
	public boolean deleteProfilePicture(Long employeeId) {
	    Employee emp = employeeRepository.findById(employeeId)
	            .orElseThrow(() -> new RuntimeException("Employee not found"));

	    try {
	        if (emp.getProfilePicturePath() != null) {
	            cloudinaryService.deleteFile(emp.getProfilePictureUrl());
	        }

	        emp.setProfilePicturePath(null);
	        emp.setProfilePictureUrl(null);
	        employeeRepository.save(emp);
	        return true;
	    } catch (Exception e) {
	        throw new CustomeException("Delete failed: " + e.getMessage());
	    }
	}


	
	@Transactional
	@Override
	public EmployeeDTO webClockin(Long employeeId) throws EmployeeNotFoundException  {

	   
	    Employee employee = employeeRepository.findById(employeeId)
	            .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));

	    EmployeeLogInDetails existing =
	            empLoginRepo.findTopByEmployeeAndLogoutTimeIsNull(employee);

	    if (existing != null) {
	    	throw new EmployeeAlreadyLoggedInException("Employee already logged in");
	    }

	    List<EmployeeLogInDetails> allRecords =
	            empLoginRepo.findByEmployee(employee);

	    boolean checkedOutToday = allRecords.stream()
	            .anyMatch(r ->
	                    r.getLoginTime().toLocalDate().equals(LocalDate.now())
	                            && r.getLogoutTime() != null);

	    if (checkedOutToday) {
	    	throw new AlreadyCheckedOutException("Employee already checked out today");
	    }

	    EmployeeLogInDetails login = new EmployeeLogInDetails();
	    login.setEmployee(employee);
	    login.setLoginTime(LocalDateTime.now());
	    login.setDayStatus(EmployeeDayStatus.PRESENT + " | Missing Swipe");

	    LocalTime cutOff = LocalTime.of(9, 30);

	    if (!LocalTime.now().isAfter(cutOff)) {
	        login.setArrivalTime("On Time");
	    } else {
	        Duration d = Duration.between(cutOff, login.getLoginTime());
	        login.setArrivalTime(
	                String.format("%02d:%02d:%02d Late",
	                        d.toHours(),
	                        d.toMinutes() % 60,
	                        d.getSeconds() % 60)
	        );
	    }

	    EmployeeLogInDetails saved = empLoginRepo.save(login);

	    EmployeeDTO dto = new EmployeeDTO();
	    dto.setId(employee.getId());
	    dto.setFirstName(employee.getFirstName());
	    dto.setLastName(employee.getLastName());
	    dto.setDisplayName(employee.getFirstName() + " " + employee.getLastName());
	    dto.setLoginTime(saved.getLoginTime());
	    dto.setLogoutTime(saved.getLogoutTime());
	    dto.setDuration(saved.getTotalTime());
	    dto.setDayStatus(saved.getDayStatus());
	    dto.setArrivalTime(saved.getArrivalTime());

	    return dto;
	}

	@Transactional
	@Override
	public EmployeeDTO webClockout(Long employeeId) {

	  
	    Employee employee = employeeRepository.findById(employeeId)
	            .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));
	    


	    EmployeeLogInDetails details =
	            empLoginRepo.findTopByEmployeeAndLogoutTimeIsNull(employee);

	    if (details == null) {
	    	throw new EmployeeNotLoggedInException("Employee not logged in");
	    }

	    LocalDateTime logoutTime = LocalDateTime.now();
	    details.setLogoutTime(logoutTime);

	    Duration duration = Duration.between(details.getLoginTime(), logoutTime);

	    String total = String.format("%02d:%02d:%02d",
	            duration.toHours(),
	            duration.toMinutes() % 60,
	            duration.getSeconds() % 60);

	    details.setTotalTime(total);

	    if (duration.toHours() >= 9) {
	        details.setDayStatus(EmployeeDayStatus.PRESENT+" | Missing Swipe");
	    } else if (duration.toHours() > 4) {
	        details.setDayStatus(EmployeeDayStatus.HALFDAY+" | Missing Swipe");
	    } else {
	        details.setDayStatus("Less Than 4 hours");
	    }
	    empLoginRepo.save(details);

	    EmployeeDTO dto = new EmployeeDTO();
	    dto.setId(employee.getId());
	    dto.setFirstName(employee.getFirstName());
	    dto.setLastName(employee.getLastName());
	    dto.setDisplayName(employee.getFirstName() + " " + employee.getLastName());
	    dto.setLoginTime(details.getLoginTime());
	    dto.setLogoutTime(logoutTime);
	    dto.setDuration(total);
//	    dto.setEmpStatus(employee.getEmpStatus());
	    dto.setDayStatus(details.getDayStatus());
	    dto.setArrivalTime(details.getArrivalTime());

	    return dto;
	}
	


	@Transactional
	@Override
	public EmployeeLeaveDTO applyLeave(EmployeeLeaveDTO dto) {

	    //  Validate dates
	    if (dto.getEnd().isBefore(dto.getStart())) {
	        throw new InvalidDateRangeException("End date cannot be before start date");
	    }

	    //  Fetch Employee USING employeeId ONLY
	    Employee employee = employeeRepository.findById(dto.getEmployeeId())
	            .orElseThrow(() -> new EmployeeNotFoundException("Invalid Employee Id"));
	    System.out.println(employee);

	    int year = LocalDate.now().getYear();

	    //  Fetch Leave Balance
	    EmployeeLeaveBalance balance =
	            leaveBalanceRepo.findByEmployeeAndLeaveTypeAndCurrentYear(
	                    employee, dto.getLeaveType(), year
	            ).orElseThrow(() ->
	                    new RuntimeException("Leave balance not initialized")
	            );

	    //  Calculate days
	    long requestedDays =
	            ChronoUnit.DAYS.between(dto.getStart(), dto.getEnd()) + 1;

	    if (balance.getRemaining() < requestedDays) {
	        throw new InsufficientLeaveBalanceException("Insufficient leave balance");
	    }
	    // s Save Leave
	    EmployeeLeaves leave = new EmployeeLeaves();
	    leave.setEmployee(employee);                 // ✅ CRITICAL
	    leave.setLeaveType(dto.getLeaveType());
	    leave.setLeaveStatus(ApprovalStatus.PENDING);
	    leave.setStartDate(dto.getStart());
	    leave.setEndDate(dto.getEnd());
	    leave.setReason(dto.getReason());
	    leave.setTotalDays(requestedDays);
	    leave.setCurrentYear(year);
	    leave.setCurrentMonth(LocalDate.now().getMonthValue());
	    leave.setAppliedDate(LocalDate.now());

	    EmployeeLeaves savedLeave = empLeaveRepo.save(leave);

	    //  Notify HR
	    notificationService.notifyHrLeaveApplied(savedLeave);

	    //  Response DTO
	    EmployeeLeaveDTO response = new EmployeeLeaveDTO();
	    response.setId(savedLeave.getId());                 // Leave ID
	    response.setEmployeeId(employee.getId());           // Employee ID
	    response.setLeaveType(savedLeave.getLeaveType());
	    response.setStart(savedLeave.getStartDate());
	    response.setEnd(savedLeave.getEndDate());
	    response.setReason(savedLeave.getReason());
	    response.setTotalDays(savedLeave.getTotalDays());
	    response.setLeaveStatus(savedLeave.getLeaveStatus());

	    return response;
	}

	@Transactional
	   public EmployeeLeaveDTO applyWFH(EmployeeLeaveDTO wfh) {

	       
	       if (wfh.getEnd().isBefore(wfh.getStart())) {
	           throw new IllegalArgumentException("End date cannot be before start date");
	       }

	      
	       Employee employee = employeeRepository.findById(wfh.getEmployeeId())
	               .orElseThrow(() -> new EmployeeNotFoundException("Invalid Employee Id"));

	      
	       EmployeeLeaveBalance empAllowedWfh =
	    		   leaveBalanceRepo.findByLeaveTypeAndEmployee_Id(
	                       EmployeeLeaveType.WFH,      
	                       employee.getId()
	               );

	      
	       if (empAllowedWfh == null) {
	           throw new RuntimeException("WFH leave balance not configured for this employee");
	       }

	       long days = ChronoUnit.DAYS.between(wfh.getStart(), wfh.getEnd()) + 1;

	       if (empAllowedWfh.getRemaining() < days) {
	           throw new RuntimeException("No more Work From Home balance available");
	       }

	     
	       empAllowedWfh.setUsed(empAllowedWfh.getUsed() + days);
	       empAllowedWfh.setRemaining(empAllowedWfh.getRemaining() - days);
	       leaveBalanceRepo.save(empAllowedWfh);

	
	       EmployeeLeaves empLeave = new EmployeeLeaves();
	       empLeave.setStartDate(wfh.getStart());
	       empLeave.setEndDate(wfh.getEnd());
	       empLeave.setLeaveType(EmployeeLeaveType.WFH);   
	       empLeave.setEmployee(employee);
	       empLeave.setReason(wfh.getReason());
	       empLeave.setTotalDays(days);
	       empLeave.setAppliedDate(LocalDate.now());
	       empLeave.setLeaveStatus(ApprovalStatus.PENDING); 

	       EmployeeLeaves empSaved = empLeaveRepo.save(empLeave);
	       notificationService.notifyHrLeaveApplied(empSaved);

	       // ✅ RESPONSE DTO
	       EmployeeLeaveDTO empResWfhDto = new EmployeeLeaveDTO();
	       empResWfhDto.setStart(empSaved.getStartDate());
	       empResWfhDto.setEnd(empSaved.getEndDate());
	       empResWfhDto.setEmployeeId(empSaved.getEmployee().getId());
	       empResWfhDto.setLeaveStatus(empSaved.getLeaveStatus());
	       empResWfhDto.setReason(empSaved.getReason());
	       empResWfhDto.setTotalDays(days);
	       empResWfhDto.setLeaveType(empSaved.getLeaveType());
	       empResWfhDto.setId(empSaved.getId());

	       return empResWfhDto;
	   }



	@Override
	public UpdateEmployeeResponseDto updateEmployee(Long employeeId, UpdateEmployeeRequestDto dto) {
		 Employee employee = employeeRepository.findById(employeeId)
	                .orElseThrow(() ->
	                        new RuntimeException("Employee not found with id: " + employeeId));

	        // ===== Update Employee fields =====
	       if(dto.getFirstName() != null) employee.setFirstName(dto.getFirstName());
	       if(dto.getLastName()!=null) employee.setLastName(dto.getLastName());
	       if(dto.getMobile()!=null)  employee.setMobile(dto.getMobile());
	       if(dto.getCardNumber()!=null) employee.setCardNumber(dto.getCardNumber());
	       if(dto.getDepartment()!=null)  employee.setDepartment(dto.getDepartment());
	       if(dto.getDesignation()!= null) employee.setDesignation(dto.getDesignation());
	       if(dto.getGender() !=null) employee.setGender(dto.getGender());
	       employee.setPaidLeaves(dto.getPaidLeaves());
            
	        // ===== Update User email =====
	        Users user = employee.getUser();
	        if (user != null && dto.getEmail() != null) { 
	            user.setEmail(dto.getEmail());
	        }

	        Employee savedEmployee = employeeRepository.save(employee);
            
	        
	        UpdateEmployeeResponseDto response = new UpdateEmployeeResponseDto();

	        response.setId(employee.getId());
	        response.setFirstName(employee.getFirstName());
	        response.setLastName(employee.getLastName());
	        response.setMobile(employee.getMobile());
	        response.setCardNumber(employee.getCardNumber());
	        response.setGender(employee.getGender());
	        response.setJoiningDate(employee.getJoiningDate());
	        response.setDaysPresent(employee.getDaysPresent());
	        response.setPaidLeaves(employee.getPaidLeaves());
            response.setDepartment(employee.getDepartment());
            response.setDesignation(employee.getDesignation());
//	         User data
	        Users user1 = employee.getUser();
	        if (user1 != null) {
	            response.setEmail(user1.getEmail());
	           
	        }

          return response;
	}

	@Override
	public String regularize(EmployeeRegulationDTO regulation) 
	{
		 Employee employee = employeeRepository.findById(regulation.getId())
		            .orElseThrow(() -> new EmployeeNotFoundException("Invalid Employee Id"));		 
		 EmployeeLogInDetails regulariseEmpDetails = empLoginRepo.findByLoginDateAndEmployee(regulation.getPunchDate(),employee)
		 .orElseThrow(()->new RuntimeException("Login Details Not Found for that Date "+regulation.getPunchDate()));

			if((regulariseEmpDetails.getLoginTime()==null||regulariseEmpDetails.getLogoutTime()==null) || (regulariseEmpDetails.getLoginTime()==null && regulariseEmpDetails.getLogoutTime()==null)&& regulariseEmpDetails.getDayStatus().equals(EmployeeDayStatus.ABSENT.toString())) {
				
				MisPunchRequest mpr = new MisPunchRequest();
				mpr.setEmployee(employee);
				mpr.setPunchDate(regulation.getPunchDate());
				mpr.setReason(regulation.getReason());
				mpr.setAppliedAt(LocalDateTime.now());
				mpr.setStatus(ApprovalStatus.PENDING);
				mpr.setLoginTime(regulation.getLoginTime());
				mpr.setLogoutTime(regulation.getLogoutTime());
				
				MisPunchRequestRepo.save(mpr);
				
			}else {
				throw new RuntimeException("cannot regularize");
			}
		
		return "Regulation Applied Successfully";
	}

	   @Override
	    public String getPayslipUrl(Long payslipId) {
	        Payslip payslip = payslipRepo.findById(payslipId)
	                .orElseThrow(() -> new CustomeException("Payslip not found"));

	        if (payslip.getPdfPath() == null) {
	            throw new CustomeException("Payslip PDF not available");
	        }

	        return payslip.getPdfPath(); 
	    }

}
	       
