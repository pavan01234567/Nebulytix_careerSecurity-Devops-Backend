package com.neb.service.impl;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.neb.dto.AddJobRequestDto;
import com.neb.dto.EmployeeBankDetailsRequest;
import com.neb.dto.EmployeeBankDetailsResponse;
import com.neb.dto.EmployeeDetailsResponseDto;
import com.neb.dto.EmployeeLeaveDTO;
import com.neb.dto.EmployeeMonthlyReportDTO;
import com.neb.dto.JobDetailsDto;
import com.neb.dto.PayslipDto;
import com.neb.dto.employee.UpdateEmployeeRequestDto;
import com.neb.dto.employee.UpdateEmployeeResponseDto;
import com.neb.dto.salary.SalaryRequestDto;
import com.neb.dto.salary.SalaryResponseDto;
import com.neb.entity.DailyReport;
import com.neb.entity.Employee;
import com.neb.entity.EmployeeBankDetails;
import com.neb.entity.EmployeeLeaveBalance;
import com.neb.entity.EmployeeLeaves;
import com.neb.entity.EmployeeMonthlyReport;
import com.neb.entity.EmployeeSalary;
import com.neb.entity.Job;
import com.neb.entity.JobApplication;
import com.neb.entity.Payslip;
import com.neb.entity.Users;
import com.neb.exception.CustomeException;
import com.neb.exception.EmployeeNotFoundException;
import com.neb.exception.NoActiveSalaryException;
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
import com.neb.repo.PayslipRepository;
import com.neb.repo.UsersRepository;
import com.neb.service.EmailService;
import com.neb.service.HrService;
import com.neb.util.ApprovalStatus;
import com.neb.util.EmployeeDayStatus;
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
    private EmailService emailService;

    @Autowired
    private ModelMapper mapper;
    
    @Autowired 
    private UsersRepository usersRepository;
    @Autowired
    private EmployeeBankDetailsRepository bankRepo;
    @Autowired
    private EmployeeLeaveRepository empLeavesRepo;

    @Value("${daily-report.folder-path}")
    private String dailyReportFolderPath;

    @Override
    public EmployeeDetailsResponseDto getEmployee(Long id) {
        Employee emp = empRepo.findById(id).orElseThrow(() -> new CustomeException("Employee not found with id: " + id));
        return mapper.map(emp, EmployeeDetailsResponseDto.class);
    }

    @Override
    public String deleteById(Long id) {
    	Employee employee = empRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

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
    public EmployeeDetailsResponseDto addAttendence(Long id, int days) {
        Employee emp = empRepo.findById(id).orElseThrow(() -> new CustomeException("Employee not found"));
        emp.setDaysPresent(days);
        return mapper.map(empRepo.save(emp), EmployeeDetailsResponseDto.class);
    }

    // ======================= PAYSLIP METHODS =========================
    @Override
    public byte[] downloadPayslip(Long payslipId) throws Exception {
        Payslip p = payslipRepo.findById(payslipId).orElseThrow(() -> new CustomeException("Payslip not found"));
        return Files.readAllBytes(Paths.get(p.getPdfPath()));
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

    // ======================= DAILY REPORT =========================
    @Override
    public String generateDailyReport(LocalDate reportDate) {
        List<DailyReport> reports = dailyReportRepository.findByReportDate(reportDate);
        if (reports.isEmpty()) return "No daily reports found for date: " + reportDate;

        try {
            byte[] pdfBytes = new ReportGeneratorPdf().generateDailyReportForEmployees(reports, reportDate);

            Path folder = Paths.get(dailyReportFolderPath);
            if (!Files.exists(folder)) Files.createDirectories(folder);

            String fileName = "daily-report-" + reportDate + ".pdf";
            Path filePath = folder.resolve(fileName);

            try (OutputStream os = Files.newOutputStream(filePath)) {
                os.write(pdfBytes);
            }

            String fileUrl = "/reports/daily/" + fileName;
            reports.forEach(r -> r.setDailyReportUrl(fileUrl));
            dailyReportRepository.saveAll(reports);

            return fileUrl;

        } catch (Exception e) {
            throw new CustomeException("Failed to generate PDF: " + e.getMessage());
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

	@Override
	public void deletePayslip(Long id) {
		
		// 1. Find payslip
        Payslip payslip = payslipRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Payslip not found with ID: " + id));

        // 2. Delete file from filesystem
        if (payslip.getPdfPath() != null) {

            File file = new File(payslip.getPdfPath());

            if (file.exists()) {
                boolean deleted = file.delete();
                if (!deleted) {
                    throw new RuntimeException("Failed to delete file from disk: " + payslip.getPdfPath());
                }
            }
        }

        // 3. Delete DB record
        payslipRepo.delete(payslip);

        
    
	}

	@Override
	@Transactional
	public SalaryResponseDto addSalary(SalaryRequestDto salRequestDto) 
	{
		 Employee employee = empRepo.findById(salRequestDto.getEmployeeId())
		            .orElseThrow(() ->new EmployeeNotFoundException("Employee not found with id: " + salRequestDto.getEmployeeId()));

        salRepo.findByEmployeeIdAndActiveTrue(employee.getId())
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
	public SalaryResponseDto getActiveSalary(Long employeeId) {
		EmployeeSalary salary = salRepo.findByEmployeeIdAndActiveTrue(employeeId)
                                .orElseThrow(() ->new SalaryNotFoundException("Active salary not found for employeeId: " + employeeId));

       return mapper.map(salary, SalaryResponseDto.class);
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
	 @Override
	    public EmployeeBankDetailsResponse addOrUpdateBankDetails(
	            Long employeeId,
	            EmployeeBankDetailsRequest request) {

	        Employee employee = empRepo.findById(employeeId)
	                .orElseThrow(() -> new RuntimeException("Employee not found"));

	        EmployeeBankDetails bankDetails = bankRepo
	                .findByEmployeeId(employeeId)
	                .orElse(new EmployeeBankDetails());

	        bankDetails.setEmployee(employee);
	        bankDetails.setBankAccountNumber(request.getBankAccountNumber());
	        bankDetails.setIfscCode(request.getIfscCode());
	        bankDetails.setBankName(request.getBankName());
	        bankDetails.setPfNumber(request.getPfNumber());
	        bankDetails.setPanNumber(request.getPanNumber());
	        bankDetails.setUanNumber(request.getUanNumber());
	        bankDetails.setEpsNumber(request.getEpsNumber());
	        bankDetails.setEsiNumber(request.getEsiNumber());

	        EmployeeBankDetails saved = bankRepo.save(bankDetails);

	        return new EmployeeBankDetailsResponse(
	                employee.getId(),
	                saved.getBankAccountNumber(),
	                saved.getIfscCode(),
	                saved.getBankName(),
	                saved.getPfNumber(),
	                saved.getPanNumber(),
	                saved.getUanNumber(),
	                saved.getEpsNumber(),
	                saved.getEsiNumber()
	        );
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
		            .orElseThrow(() -> new RuntimeException("Invalid Leave Id"));

		    if (leave.getLeaveStatus() != ApprovalStatus.PENDING) {
		        throw new RuntimeException("Leave already processed");
		    }

		    if (status == ApprovalStatus.APPROVED) {

		        EmployeeLeaveBalance balance =
		                empLeaveBlnceRepo
		                        .findByEmployeeAndLeaveTypeAndCurrentYear(
		                                leave.getEmployee(),	
		                                leave.getLeaveType(),   // 🔥 use leave's type
		                                LocalDate.now().getYear()
		                        )
		                        .orElseThrow(() ->
		                                new RuntimeException("Leave balance not found"));

		        long usedAfterApproval = balance.getUsed() + leave.getTotalDays();
		        long remaining = balance.getTotalAllowed() - usedAfterApproval;

		        if (remaining < 0) {
		            throw new RuntimeException("Insufficient leave balance");
		        }

		        balance.setUsed(usedAfterApproval);
		        balance.setRemaining(remaining);

		        empLeaveBlnceRepo.save(balance);
		    }

		    leave.setLeaveStatus(status);
		    EmployeeLeaves savedLeave = empLeavesRepo.save(leave);

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


	
	
}

