package com.neb.service;

import java.time.LocalDate;
import java.util.List;


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
import com.neb.entity.EmployeeBankDetails;
import com.neb.entity.JobApplication;
import com.neb.util.ApprovalStatus;

public interface HrService {
   
    //List<EmployeeDetailsResponseDto> getEmployeeList();
    EmployeeDetailsResponseDto getEmployee(Long id);
    String deleteById(Long id);
    EmployeeDetailsResponseDto addAttendence(Long id, int days);
  
  
    // Payslip
    byte[] downloadPayslip(Long payslipId) throws Exception;
    List<PayslipDto> listPayslipsForEmployee(Long employeeId);

    // Job management
    JobDetailsDto addJob(AddJobRequestDto jobRequestDto);
    List<JobDetailsDto> getAllJobs();
    String deleteJob(Long jobId);

    // Daily report
    String generateDailyReport(LocalDate reportDate);
    String getDailyReportUrl(LocalDate reportDate);

    // Job application
    void updateJobApplicationStatus(Long applicationId, Boolean status);
    void sendInvitedEmailAndUpdateStatus(Long applicantId, String subject, String message);
    void sendRejectedEmailAndUpdateStatus(Long applicantId, String subject, String message);

    List<JobApplication> sendEmailsToShortlisted(String subject, String message);
    List<JobApplication> sendEmailsToRejected(String subject, String message);

    // Single applicant email
    void sendEmailToSingleApplicant(Long applicantId, String subject, String message);
    
    public void deletePayslip(Long id); 
    
    public SalaryResponseDto addSalary(SalaryRequestDto salRequestDto);
	public SalaryResponseDto getActiveSalary(Long employeeId);
	public List<SalaryResponseDto> getAllActiveSalaries();
	public SalaryResponseDto updateSalary(Long salaryId, SalaryRequestDto dto);
	public String deleteSalary(Long salaryId);
	 EmployeeBankDetailsResponse addOrUpdateBankDetails(
	            Long employeeId,
	            EmployeeBankDetailsRequest request
	    );
	 public List<EmployeeLeaveDTO> leaves(ApprovalStatus status);
	 public EmployeeLeaveDTO approvalOrReject(Long leaveId, ApprovalStatus status) ;
	 public List<EmployeeMonthlyReportDTO> generateMontlyReport();
//	 private Boolean reportAlreadyExists(Employee employee, int year, int month) ;
	 public EmployeeMonthlyReportDTO getMonthlyReportOfEmployee(Long employeeId, Integer year, Integer month);
	 public List<EmployeeLeaveDTO> employeeOnLeave();

}
