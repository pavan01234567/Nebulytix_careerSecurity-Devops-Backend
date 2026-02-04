package com.neb.service.impl;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.neb.constants.Role;
import com.neb.dto.AddJobRequestDto;
import com.neb.dto.AssignLeaveBalanceDTO;
import com.neb.dto.EmployeeDetailsResponseDto;
import com.neb.dto.EmployeeLeaveBalanceDTO;
import com.neb.dto.EmployeeLeaveDTO;
import com.neb.dto.EmployeeMonthlyReportDTO;
import com.neb.dto.EmployeeRegulationDTO;
import com.neb.dto.JobDetailsDto;
import com.neb.dto.PayslipDto;
import com.neb.dto.TodayAttendanceCountDTO;
import com.neb.dto.employee.EmployeeProfileDto;
import com.neb.dto.salary.SalaryRequestDto;
import com.neb.dto.salary.SalaryResponseDto;
import com.neb.entity.DailyReport;
import com.neb.entity.Employee;
import com.neb.entity.EmployeeLeaveBalance;
import com.neb.entity.EmployeeLeaves;
import com.neb.entity.EmployeeLogInDetails;
import com.neb.entity.EmployeeMonthlyReport;
import com.neb.entity.EmployeeSalary;
import com.neb.entity.Job;
import com.neb.entity.JobApplication;
import com.neb.entity.MisPunchRequest;
import com.neb.entity.Payslip;
import com.neb.entity.Users;
import com.neb.exception.CustomeException;
import com.neb.exception.EmployeeNotFoundException;
import com.neb.exception.InvalidActionException;
import com.neb.exception.LeaveOperationException;
import com.neb.exception.NoActiveSalaryException;
import com.neb.exception.ResourceNotFoundException;
import com.neb.exception.SalaryNotFoundException;
import com.neb.repo.DailyReportRepository;
import com.neb.repo.EmployeeBankDetailsRepository;
import com.neb.repo.EmployeeLeaveBalanceRepo;
import com.neb.repo.EmployeeLeaveRepository;
//import com.neb.repo.EmployeeLeaveType;
import com.neb.repo.EmployeeLoginDetailsRepo;
import com.neb.repo.EmployeeMontlyReportRepo;
import com.neb.repo.EmployeeRepository;
import com.neb.repo.EmployeeSalaryRepository;
import com.neb.repo.JobApplicationRepository;
import com.neb.repo.JobRepository;
import com.neb.repo.MisPunchRequestRepo;
import com.neb.repo.PayslipRepository;
import com.neb.repo.ProjectRepository;
import com.neb.repo.UsersRepository;
import com.neb.service.CloudinaryService;
import com.neb.service.EmailService;
import com.neb.service.HrService;
import com.neb.service.NotificationService;
import com.neb.util.ApprovalStatus;
import com.neb.util.EmployeeDayStatus;
import com.neb.util.EmployeeLeaveType;
import com.neb.util.ReportGeneratorPdf;

import jakarta.transaction.Transactional;

@Service
public class HrServiceImpl implements HrService {

   
	@Autowired
    private EmployeeRepository empRepo;
    @Autowired
	private EmployeeLeaveBalanceRepo empLeaveBlnceRepo;
	@Autowired
	private EmployeeMontlyReportRepo employeeMonthlyReportRepo;
    @Autowired
	private EmployeeLoginDetailsRepo empLoginRepo;
    @Autowired
    private EmployeeSalaryRepository salRepo;
    @Autowired
    private PayslipRepository payslipRepo;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private DailyReportRepository dailyReportRepository;
    @Autowired
    private JobApplicationRepository jobApplicationRepository;
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmailService emailService;
    @Autowired
    private ModelMapper mapper;
    @Autowired 
    private UsersRepository usersRepository;
    @Autowired
    private EmployeeBankDetailsRepository bankRepo;
    @Autowired
    private EmployeeLeaveRepository empLeavesRepo;
    
    @Autowired
    private ProjectRepository projectRepo;

    @Autowired
    private MisPunchRequestRepo misPunchRequestRepo;

   
    @Autowired
    private CloudinaryService cloudinaryService;

    @Value("${cloudinary.folder.reports}")
    private String reportsFolder;

    @Override
    public EmployeeDetailsResponseDto getEmployee(Long id) {
        Employee emp = empRepo.findById(id)
                .orElseThrow(() -> new CustomeException("Employee not found"));
        return mapper.map(emp, EmployeeDetailsResponseDto.class);
    }
    @Override
    public String deleteById(Long id) {
    	    Employee employee = empRepo.findByIdIncludingInactive(id)
    	                       .orElseThrow(() ->new EmployeeNotFoundException("Employee not found with id: " + id));
            if (!"inactive".equalsIgnoreCase(employee.getEmpStatus())) {
    	        throw new CustomeException("Employee must be disabled before permanent deletion with id: " + id);
    	    }

    	    Users user = employee.getUser();

            if (user != null && user.isEnabled()) {
    	        throw new CustomeException("User account must be disabled before deleting employee with id: " + id);
    	    }

            employee.setUser(null);
            empRepo.delete(employee);

    	    if (user != null) {
    	        usersRepository.delete(user);
    	    }

    	    return "Employee permanently deleted with id: " + id;
    }
  
    @Override
	public String disableEmp(Long id) {
    	
    	Employee employee = empRepo.findById(id)
                .orElseThrow(() ->
                        new EmployeeNotFoundException("Employee not found with id: " + id));

        if ("inactive".equalsIgnoreCase(employee.getEmpStatus())) {
        	throw new CustomeException("Employee already disabled with id: " + id);
        }

        employee.setEmpStatus("inactive");

        Users user = employee.getUser();
        if (user != null && user.isEnabled()) {
            user.setEnabled(false);
            usersRepository.save(user);
        }

        empRepo.save(employee);

        return "Employee disabled successfully with id: " + id;

	}

	@Override
	public String enableEmp(Long id) {
		 Employee employee = empRepo.findById(id).orElseThrow(() ->new EmployeeNotFoundException("Employee not found with id: " + id));
           System.out.println(employee);
		    if (!"inactive".equalsIgnoreCase(employee.getEmpStatus())) {
	              throw new CustomeException("Employee already enabled with id: " + id);
	        }

	        employee.setEmpStatus("active");

	        Users user = employee.getUser();
	        if (user != null && !user.isEnabled()) {
	            user.setEnabled(true);
	            usersRepository.save(user);
	        }

	        empRepo.save(employee);

	        return "Employee enabled successfully with id: " + id;

	}
    @Override
    public EmployeeDetailsResponseDto addAttendence(Long id, int days) {
        Employee emp = empRepo.findById(id).orElseThrow(() -> new CustomeException("Employee not found"));
        emp.setDaysPresent(days);
        return mapper.map(empRepo.save(emp), EmployeeDetailsResponseDto.class);
    }

    // ======================= PAYSLIP METHODS =========================
//    @Override
//    public byte[] downloadPayslip(Long payslipId) throws Exception {
//        Payslip p = payslipRepo.findById(payslipId).orElseThrow(() -> new CustomeException("Payslip not found"));
//        return Files.readAllBytes(Paths.get(p.getPdfPath()));
//    }
    @Override
    public String getPayslipUrl(Long payslipId) {
        Payslip payslip = payslipRepo.findById(payslipId)
                .orElseThrow(() -> new CustomeException("Payslip not found"));
        return payslip.getPdfUrl();
    }

    @Override
    public List<PayslipDto> listPayslipsForEmployee(Long employeeId) {
        List<Payslip> payslips = payslipRepo.findByEmployeeId(employeeId);
        if (payslips.isEmpty()) throw new CustomeException("No payslips found");
        return payslips.stream().map(PayslipDto::fromEntity).collect(Collectors.toList());
    }

    // ======================= JOB METHODS =========================
    @Override
    public JobDetailsDto addJob(AddJobRequestDto jobRequestDto) {
        Job job = mapper.map(jobRequestDto, Job.class);
        job.setIsActive(true);
        job.setPostedDate(jobRequestDto.getPostedDate() != null ? jobRequestDto.getPostedDate() : LocalDate.now());
        return mapper.map(jobRepository.save(job), JobDetailsDto.class);
    }

    @Override
    public List<JobDetailsDto> getAllJobs() {
        LocalDate today = LocalDate.now();
        return jobRepository.findAll().stream().map(job -> {
            job.setIsActive(job.getClosingDate() == null || !job.getClosingDate().isBefore(today));
            return mapper.map(job, JobDetailsDto.class);
        }).collect(Collectors.toList());
    }

    @Override
    public String deleteJob(Long jobId) {
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new RuntimeException("Job not found"));
        jobRepository.delete(job);
        return "Job deleted successfully";
    }
    @Override
    public String generateDailyReport(LocalDate reportDate) {
        List<DailyReport> reports = dailyReportRepository.findReportsWithEmployee(reportDate);

        if (reports.isEmpty()) {
            return null; // Changed to null to trigger your Controller's 404 logic
        }

        try {
            byte[] pdfBytes = new ReportGeneratorPdf()
                    .generateDailyReportForEmployees(reports, reportDate);

            String fileName = "daily-report-" + reportDate;

            // ✅ FIX: Change "raw" to "image" 
            // This enables HTTPS browser preview and removes the insecure connection warning
            String reportUrl = cloudinaryService.uploadFile(
                    pdfBytes,
                    fileName,
                    reportsFolder,
                    "image" 
            );

            reports.forEach(r -> r.setDailyReportUrl(reportUrl));
            dailyReportRepository.saveAll(reports);

            return reportUrl;

        } catch (Exception e) {
            throw new CustomeException("Daily report upload failed: " + e.getMessage());
        }
    }

    @Override
    public String getDailyReportUrl(LocalDate reportDate) {
        List<DailyReport> reports = dailyReportRepository.findByReportDate(reportDate);
        return reports.isEmpty() ? null : reports.get(0).getDailyReportUrl();
    }

    // ======================= JOB APPLICATION =========================
    @Override
    public void updateJobApplicationStatus(Long applicationId, Boolean status) {
        JobApplication app = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        app.setStatus(status ? "SHORTLISTED" : "REJECTED");
        jobApplicationRepository.save(app);
    }

    @Override
    public List<JobApplication> sendEmailsToShortlisted(String subject, String message) {
        List<JobApplication> shortlisted = jobApplicationRepository.findByStatus("SHORTLISTED");

        if (shortlisted.isEmpty()) {
            throw new RuntimeException("No shortlisted applicants found");
        }

        for (JobApplication app : shortlisted) {
            emailService.sendApplicationMail(app.getEmail(), subject, message);
            app.setStatus("INVITED"); // update status after sending email
            jobApplicationRepository.save(app);
        }

        return shortlisted; // return updated applicants
    }

    @Override
    public List<JobApplication> sendEmailsToRejected(String subject, String message) {
        List<JobApplication> rejected = jobApplicationRepository.findByStatus("REJECTED");

        if (rejected.isEmpty()) {
            throw new RuntimeException("No rejected applicants found");
        }

        for (JobApplication app : rejected) {
            emailService.sendApplicationMail(app.getEmail(), subject, message);
            app.setStatus("TERMINATED"); // update status after sending email
            jobApplicationRepository.save(app);
        }

        return rejected; // return updated applicants
    }


    @Override
    public void sendEmailToSingleApplicant(Long applicantId, String subject, String message) {
        String email = jobApplicationRepository.findEmailByApplicationId(applicantId);
        if (email == null) throw new RuntimeException("Applicant not found for ID: " + applicantId);
        emailService.sendEmail(email, subject, message);
    }

    @Override
    public void sendInvitedEmailAndUpdateStatus(Long applicantId, String subject, String message) {
        JobApplication app = jobApplicationRepository.findById(applicantId)
                .orElseThrow(() -> new RuntimeException("Applicant not found"));
        emailService.sendApplicationMail(app.getEmail(), subject, message);
        app.setStatus("INVITED");
        jobApplicationRepository.save(app);
    }

    @Override
    public void sendRejectedEmailAndUpdateStatus(Long applicantId, String subject, String message) {
        JobApplication app = jobApplicationRepository.findById(applicantId)
                .orElseThrow(() -> new RuntimeException("Applicant not found"));
        emailService.sendApplicationMail(app.getEmail(), subject, message);
        app.setStatus("TERMINATED");
        jobApplicationRepository.save(app);
    }

//	@Override
//	public void deletePayslip(Long id) {
//		
//		//  Find payslip
//        Payslip payslip = payslipRepo.findById(id)
//                .orElseThrow(() -> new RuntimeException("Payslip not found with ID: " + id));
//
//        //  Delete file from filesystem
//        if (payslip.getPdfPath() != null) {
//
//            File file = new File(payslip.getPdfPath());
//
//            if (file.exists()) {
//                boolean deleted = file.delete();
//                if (!deleted) {
//                    throw new RuntimeException("Failed to delete file from disk: " + payslip.getPdfPath());
//                }
//            }
//        }
//
//        //  Delete DB record
//        payslipRepo.delete(payslip);
//
//        
//    
//	}
    @Override
    public void deletePayslip(Long id) {

        Payslip payslip = payslipRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Payslip not found"));

        // pdfPath = Cloudinary public_id
        if (payslip.getPdfPath() != null) {
            cloudinaryService.deleteFile(payslip.getPdfPath());
        }

        payslipRepo.delete(payslip);
    }

//	@Override
//	@Transactional
//	public SalaryResponseDto addSalary(SalaryRequestDto salRequestDto) 
//	{
//		 Employee employee = empRepo.findById(salRequestDto.getEmployeeId())
//		            .orElseThrow(() ->new EmployeeNotFoundException("Employee not found with id: " + salRequestDto.getEmployeeId()));
//
//        salRepo.findByEmployeeIdAndActiveTrue(employee.getId())
//                .ifPresent(existing -> {
//                    if (!salRequestDto.getEffectiveFrom().isAfter(LocalDate.now())) {
//                        existing.setActive(false);
//                        salRepo.save(existing);
//                    }
//                });
//
//        EmployeeSalary salary = mapper.map(salRequestDto, EmployeeSalary.class);
//               salary.setId(null);
//               salary.setEmployee(employee);
//               salary.setActive(!salRequestDto.getEffectiveFrom().isAfter(LocalDate.now()));
//               
//       return  mapper.map(salRepo.save(salary),SalaryResponseDto.class);
//	}
//
//	@Override
//	public SalaryResponseDto getActiveSalary(Long employeeId) {
//		  Optional<EmployeeSalary> salaryopt = salRepo.findByEmployeeIdAndActiveTrue(employeeId);
//             if(salaryopt != null) {
//            	 return mapper.map(salaryopt.get(), SalaryResponseDto.class); 
//             }
//             else {
//            	 return null;
//             }
//	}
	@Override
	@Transactional
	public SalaryResponseDto addSalary(SalaryRequestDto salRequestDto) 
	{
		 Employee employee = empRepo.findById(salRequestDto.getEmployeeId())
		            .orElseThrow(() ->new EmployeeNotFoundException("Employee not found with id: " + salRequestDto.getEmployeeId()));

        salRepo.findByEmployee_IdAndActiveTrue(employee.getId())
                .ifPresent(existing -> {
                    if (!salRequestDto.getEffectiveFrom().isAfter(LocalDate.now())) {
                        existing.setActive(false);
                        salRepo.save(existing);
                    }
                });

        EmployeeSalary salary = mapper.map(salRequestDto, EmployeeSalary.class);
               salary.setId(null);
               salary.setEmployee(employee);
               salary.setActive(!salRequestDto.getEffectiveFrom().isAfter(LocalDate.now()));
               
       return  mapper.map(salRepo.save(salary),SalaryResponseDto.class);
	}

 
	@Override
	@Transactional
	public SalaryResponseDto getActiveSalary(Long employeeId) {

	    Optional<EmployeeSalary> optionalSalary =
	            salRepo.findByEmployee_IdAndActiveTrue(employeeId);

	    // ✅ Do NOT throw exception if not found
	    if (optionalSalary.isEmpty()) {
	        return null;
	    }

	    EmployeeSalary salary = optionalSalary.get();

	    SalaryResponseDto dto = new SalaryResponseDto();
	    dto.setId(salary.getId());
	    dto.setEmployeeId(salary.getEmployee().getId());
	    dto.setBasicSalary(salary.getBasicSalary());
	    dto.setHra(salary.getHra());
	    dto.setAllowance(salary.getAllowance());
	    dto.setDeductions(salary.getDeductions());
	    dto.setNetSalary(salary.getNetSalary());
	    dto.setEffectiveFrom(salary.getEffectiveFrom());
	    dto.setActive(salary.isActive());

	    return dto;
	}

	@Override
	public List<SalaryResponseDto> getAllActiveSalaries() {
		 List<EmployeeSalary> activeSalaries = salRepo.findByActiveTrue();
            if (activeSalaries.isEmpty()) {
            	  throw new NoActiveSalaryException("No active salaries found");
	        }

	        return activeSalaries.stream()
	                .map(salary -> mapper.map(salary, SalaryResponseDto.class))
	                .toList();
	  }
	
	@Override
	@Transactional
	public SalaryResponseDto updateSalary(Long salaryId, SalaryRequestDto dto) {

	    EmployeeSalary salary = salRepo.findById(salaryId)
	            .orElseThrow(() ->new SalaryNotFoundException("Salary not found with id: " + salaryId));

	    salary.setBasicSalary(dto.getBasicSalary());
	    salary.setHra(dto.getHra());
	    salary.setAllowance(dto.getAllowance());
	    salary.setDeductions(dto.getDeductions());
	    salary.setNetSalary(dto.getNetSalary());
	    salary.setEffectiveFrom(dto.getEffectiveFrom());

	    return mapper.map(salRepo.save(salary), SalaryResponseDto.class);
	}

	@Override
	@Transactional
	public String deleteSalary(Long salaryId) {
		 EmployeeSalary salary = salRepo.findById(salaryId)
		            .orElseThrow(() -> new SalaryNotFoundException("Salary not found with id: " + salaryId));

		    //  Prevent redundant operation
		    if (!salary.isActive()) {
		        return "Salary is already inactive";
		    }

		    // Soft deactivate
		    salary.setActive(false);
		    salRepo.save(salary);

		    return "Salary deactivated successfully";

	}

    public List<EmployeeLeaveDTO> leaves(ApprovalStatus status) {

			List<EmployeeLeaves> pendingLeaves = empLeavesRepo.findByLeaveStatus(status);

			List<EmployeeLeaveDTO> employeeLeavePendingDto = pendingLeaves.stream().map(pending -> {
				EmployeeLeaveDTO leaveDto = new EmployeeLeaveDTO();
				leaveDto.setEmployeeId(pending.getEmployee().getId());
				leaveDto.setStart(pending.getStartDate());
				leaveDto.setEnd(pending.getEndDate());
				leaveDto.setLeaveType(pending.getLeaveType());
				leaveDto.setReason(pending.getReason());
				leaveDto.setTotalDays(pending.getTotalDays());
				leaveDto.setLeaveStatus(pending.getLeaveStatus());
				leaveDto.setId(pending.getId());

				return leaveDto;
			}).toList();

			return employeeLeavePendingDto;
		}


    @Transactional
    public EmployeeLeaveDTO approvalOrReject(Long leaveId, ApprovalStatus status) {

       
        EmployeeLeaves leave = empLeavesRepo.findById(leaveId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Leave not found for ID: " + leaveId));

       
        if (leave.getLeaveStatus() != ApprovalStatus.PENDING) {
            throw new InvalidActionException("This leave is already processed");
        }

   
        if (status == ApprovalStatus.APPROVED) {

            
            int leaveYear = leave.getCurrentYear();

            EmployeeLeaveBalance balance =
                    empLeaveBlnceRepo
                            .findByEmployeeAndLeaveTypeAndCurrentYear(
                                    leave.getEmployee(),
                                    leave.getLeaveType(),
                                    leaveYear
                            )
                            .orElseThrow(() ->
                                    new RuntimeException("Leave balance not found"));

            long usedAfterApproval = balance.getUsed() + leave.getTotalDays();
            long remaining = balance.getTotalAllowed() - usedAfterApproval;

            if (remaining < 0) {
                throw new LeaveOperationException("Insufficient leave balance");
            }

            balance.setUsed(usedAfterApproval);
            balance.setRemaining(remaining);

            empLeaveBlnceRepo.save(balance);
        }

       
        leave.setLeaveStatus(status);
        EmployeeLeaves savedLeave = empLeavesRepo.save(leave);

        notificationService.notifyEmployeeLeaveDecision(savedLeave);

        
        return new EmployeeLeaveDTO(
                savedLeave.getEmployee().getId(),
                savedLeave.getId(),
                savedLeave.getLeaveType(),
                savedLeave.getStartDate(),
                savedLeave.getEndDate(),
                savedLeave.getReason(),
                savedLeave.getTotalDays(),
                savedLeave.getLeaveStatus()
        );
    }


		@Transactional
		public List<EmployeeMonthlyReportDTO> generateMontlyReport() {
			List<Employee> findAllEmployees = empRepo.findAll();
			
			List<EmployeeMonthlyReportDTO> collect = findAllEmployees.stream().filter(emp -> !reportAlreadyExists(emp, LocalDate.now().getYear(), LocalDate.now().getMonthValue())).map(emp->{
				
			
				long presentDaysCount = empLoginRepo.findByEmployeeAndDayStatus(emp,EmployeeDayStatus.PRESENT.toString()).stream().count();

				long halfDayCount = empLoginRepo.findByEmployeeAndDayStatus(emp,EmployeeDayStatus.HALFDAY.toString()).stream().count();

				long casualLeaveCount = empLeavesRepo
						.findByEmployeeAndLeaveTypeAndLeaveStatus(emp, EmployeeLeaveType.CASUAL, ApprovalStatus.APPROVED)
						.stream().count();
				long earnedLeaveCount = empLeavesRepo
						.findByEmployeeAndLeaveTypeAndLeaveStatus(emp, EmployeeLeaveType.EARNED, ApprovalStatus.APPROVED)
						.stream().count();
				long sickLeaveCount = empLeavesRepo
						.findByEmployeeAndLeaveTypeAndLeaveStatus(emp, EmployeeLeaveType.SICK, ApprovalStatus.APPROVED).stream()
						.count();
				long wfhCount = empLeavesRepo
						.findByEmployeeAndLeaveTypeAndLeaveStatus(emp, EmployeeLeaveType.WFH, ApprovalStatus.APPROVED).stream()
						.count();
				long compOffLeaveCount = empLeavesRepo
						.findByEmployeeAndLeaveTypeAndLeaveStatus(emp, EmployeeLeaveType.COMPOFF, ApprovalStatus.APPROVED)
						.stream().count();
				long absentCount = empLoginRepo.findByEmployeeAndDayStatus(emp,EmployeeDayStatus.ABSENT.toString()).stream().count();

				long totalWorkingDays = YearMonth.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue()).lengthOfMonth();
				Double halfDayCountTotal = (halfDayCount)/2.0;
				Long totalLeavesCount= casualLeaveCount+earnedLeaveCount+sickLeaveCount+compOffLeaveCount;
				Double totalDaysCount = totalLeavesCount+halfDayCountTotal+wfhCount+presentDaysCount;
				
				
				EmployeeMonthlyReport empMonthlyReport = new EmployeeMonthlyReport();
				empMonthlyReport.setAbsentDays(absentCount);
				empMonthlyReport.setCurrentMonth(LocalDate.now().getMonth().getValue());
				empMonthlyReport.setCurrentYear(LocalDate.now().getYear());
				empMonthlyReport.setEmployee(emp);
				empMonthlyReport.setLeavesApplied(totalLeavesCount);
				empMonthlyReport.setPresentDays(totalDaysCount);
				empMonthlyReport.setTotalWorkingDays(totalWorkingDays);
				empMonthlyReport.setWfhDays(wfhCount);
				
				EmployeeMonthlyReport empMonthlySavedData = employeeMonthlyReportRepo.save(empMonthlyReport);
				EmployeeMonthlyReportDTO empReportDtoRes = new EmployeeMonthlyReportDTO(empMonthlySavedData);
				return empReportDtoRes;
			}).collect(Collectors.toList());
			return collect;
		}
		//helper method to tell weather the report exist or not for the employee
		private Boolean reportAlreadyExists(Employee employee, int year, int month) {

		    // check duplicate
		    boolean present = employeeMonthlyReportRepo
		        .findByEmployeeAndCurrentYearAndCurrentMonth(employee, year, month)
		        .isPresent();
		    return present;
		}
		
		//to Fetch the Report generated of the Existing employee 
		public EmployeeMonthlyReportDTO getMonthlyReportOfEmployee(Long id, Integer year, Integer month) {
			EmployeeMonthlyReport empMonthReport = employeeMonthlyReportRepo.findByIdAndCurrentYearAndCurrentMonth(id,year,month)
			.orElseThrow(()-> new RuntimeException("Employee Report Not Found"));
			
			EmployeeMonthlyReportDTO empMonthReportResDto = new EmployeeMonthlyReportDTO(empMonthReport);
			
			
			return empMonthReportResDto;
		}
		
		public List<EmployeeLeaveDTO> employeeOnLeave() {
		    List<EmployeeLeaves> empOnLeavesToday = empLeavesRepo
		        .findByAppliedDateAndCurrentYearAndCurrentMonth(
		            LocalDate.now(),
		            LocalDate.now().getYear(),
		            LocalDate.now().getMonthValue()
		        );

		    return empOnLeavesToday.stream().map(emp -> {
		        EmployeeLeaveDTO leaveResDto = new EmployeeLeaveDTO();
		        leaveResDto.setId(emp.getId());
		        leaveResDto.setEmployeeId(emp.getEmployee().getId());
		        leaveResDto.setLeaveType(emp.getLeaveType());
		        leaveResDto.setStart(emp.getStartDate());
		        leaveResDto.setEnd(emp.getEndDate());
		        leaveResDto.setReason(emp.getReason());
		        leaveResDto.setTotalDays(emp.getTotalDays());
		        leaveResDto.setLeaveStatus(emp.getLeaveStatus());
		        return leaveResDto;
		    }).toList();
		}
		
		 @Override
		    public List<EmployeeLeaveBalanceDTO> assignLeaveBalance(AssignLeaveBalanceDTO dto) {

		        Employee employee = empRepo.findById(dto.getEmployeeId())
		                .orElseThrow(() -> new EmployeeNotFoundException("Invalid Employee ID"));

		        List<EmployeeLeaveBalanceDTO> responseList = new ArrayList<>();

		        dto.getLeaveAllocation().forEach((leaveTypeStr, allowedDays) -> {

		            EmployeeLeaveType leaveType = EmployeeLeaveType.valueOf(leaveTypeStr.toUpperCase());

		            responseList.add(saveOrUpdateBalance(employee, leaveType, allowedDays, dto.getYear()));
		        });

		        return responseList;
		    }

//		    
		 private EmployeeLeaveBalanceDTO saveOrUpdateBalance(
			        Employee employee, EmployeeLeaveType type, Long allowed, Integer year) {

			    Integer leaveYear = (year != null) ? year : LocalDate.now().getYear();

			    EmployeeLeaveBalance balance = empLeaveBlnceRepo
			            .findByEmployeeAndLeaveTypeAndCurrentYear(employee, type, leaveYear)
			            .orElse(null);

			    if (balance == null) {
			        balance = new EmployeeLeaveBalance();
			        balance.setEmployee(employee);
			        balance.setLeaveType(type);
			        balance.setCurrentYear(leaveYear); // never null now
			    }

			    balance.setTotalAllowed(allowed);
			    balance.setUsed(0L);
			    balance.setRemaining(allowed);

			    EmployeeLeaveBalance saved = empLeaveBlnceRepo.save(balance);

			    EmployeeLeaveBalanceDTO dto = new EmployeeLeaveBalanceDTO();
			    dto.setId(saved.getId());
			    dto.setEmployeeId(employee.getId());
			    dto.setLeaveType(EmployeeLeaveType.valueOf(saved.getLeaveType().name()));
			    dto.setCurrentYear(saved.getCurrentYear());
			    dto.setTotalAllowed(saved.getTotalAllowed());
			    dto.setUsed(saved.getUsed());
			    dto.setRemaining(saved.getRemaining());

			    return dto;
			}

		 @Override
		    public TodayAttendanceCountDTO todayAttendanceCount() {
		        List<Employee> employees = empRepo.findAll();
		        long present = 0, wfh = 0;
		        LocalDate today = LocalDate.now();

		        for (Employee emp : employees) {
		            EmployeeLogInDetails session =
		                    empLoginRepo.findTopByEmployeeAndLogoutTimeIsNullOrderByLoginTimeDesc(emp);

		            if (session == null || session.getLoginTime() == null) continue;

		            LocalDate loginDate = session.getLoginTime()
		                    .atZone(ZoneId.systemDefault())
		                    .toLocalDate();

		            if (!loginDate.equals(today)) continue;

		            if (EmployeeDayStatus.PRESENT.name().equals(session.getDayStatus())) present++;
		            if (EmployeeDayStatus.WFH.name().equals(session.getDayStatus())) wfh++;
		        }
		        return new TodayAttendanceCountDTO(present, wfh);
		    }

		 public String regulationRejectOrApproval(Long EmployeeId, LocalDate misPunchDate, ApprovalStatus status) {

			    Employee employee = empRepo.findById(EmployeeId)
			            .orElseThrow(() -> new EmployeeNotFoundException("Invalid Id"));

			    MisPunchRequest empRegDetails = misPunchRequestRepo
			            .findByEmployeeAndPunchDate(employee, misPunchDate)
			            .orElseThrow(() -> new RuntimeException("No MisPunch record found"));

			    if (empRegDetails.getStatus() != ApprovalStatus.PENDING) {
			        return "Already approved as " + empRegDetails.getStatus();
			    }

			    empRegDetails.setActionAt(LocalDateTime.now());
			    empRegDetails.setStatus(status);

			    if (status == ApprovalStatus.APPROVED) {
			        EmployeeLogInDetails empLoginDetails = empLoginRepo
			                .findByLoginDateAndEmployee(misPunchDate, employee)
			                .orElseThrow(() -> new RuntimeException("Login record not found"));

			        empLoginDetails.setDayStatus(EmployeeDayStatus.PRESENT.toString());
			        empLoginDetails.setLoginTime(empRegDetails.getLoginTime());
			        empLoginDetails.setLogoutTime(empRegDetails.getLogoutTime());

			        Duration duration = Duration.between(
			                empRegDetails.getLoginTime(),
			                empRegDetails.getLogoutTime()
			        );

			        String formatted = String.format(
			                "%02d:%02d:%02d",
			                duration.toHours(),
			                duration.toMinutes() % 60,
			                duration.getSeconds() % 60
			        );

			        empLoginDetails.setTotalTime(formatted);
			        empLoginRepo.save(empLoginDetails);
			    }

			    misPunchRequestRepo.save(empRegDetails);

			    return employee.getId() + " regulated successfully";
			}


		 public List<EmployeeRegulationDTO> regulation(ApprovalStatus status) {

			    ApprovalStatus effectiveStatus =
			            (status == null) ? ApprovalStatus.PENDING : status;

			    return misPunchRequestRepo.findAllByStatus(effectiveStatus)
			            .stream()
			            .map(EmployeeRegulationDTO::new)
			            .toList();
			}
		 @Override
		 public List<EmployeeProfileDto> getEmployeeList() {

		     Set<Role> excludedRoles = Set.of(Role.ROLE_ADMIN);

		     List<Employee> employees = empRepo.findEmployeesExcludingRoles(excludedRoles);

		     if (employees == null || employees.isEmpty()) {
		         throw new CustomeException("No employees found");
		     }

		     return employees.stream()
				        .map(emp -> {
				            EmployeeProfileDto dto = new EmployeeProfileDto();
				            dto.setId(emp.getId());
				            dto.setFirstName(emp.getFirstName());
				            dto.setLastName(emp.getLastName());
				            dto.setDesignation(emp.getDesignation());
				            dto.setDepartment(emp.getDepartment());
				            dto.setCardNumber(emp.getCardNumber());
				            dto.setGender(emp.getGender());
				            dto.setJoiningDate(emp.getJoiningDate());
				            dto.setSalary(emp.getSalary());
				            dto.setProfilePictureUrl(emp.getProfilePictureUrl());
				            dto.setMobile(emp.getMobile());
				            dto.setEmpStatus(emp.getEmpStatus());
				            // From User entity
				            if (emp.getUser() != null) {
				                dto.setEmail(emp.getUser().getEmail());
				                dto.setUserEnabled(emp.getUser().isEnabled());
				            }

				            return dto;
				        })
				        .collect(Collectors.toList());

		 }


		
}

