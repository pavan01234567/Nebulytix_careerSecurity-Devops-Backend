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
   
    public EmployeeDetailsResponseDto getEmployee(Long id);
    public String deleteById(Long id);
    public EmployeeDetailsResponseDto addAttendence(Long id, int days);
      // Payslip
    public byte[] downloadPayslip(Long payslipId) throws Exception;
    public List<PayslipDto> listPayslipsForEmployee(Long employeeId);
       // Job management
    public JobDetailsDto addJob(AddJobRequestDto jobRequestDto);
    public List<JobDetailsDto> getAllJobs();
    public String deleteJob(Long jobId);
      // Daily report
    public String generateDailyReport(LocalDate reportDate);
    public String getDailyReportUrl(LocalDate reportDate);
      // Job application
    public void updateJobApplicationStatus(Long applicationId, Boolean status);
    public void sendInvitedEmailAndUpdateStatus(Long applicantId, String subject, String message);
    public void sendRejectedEmailAndUpdateStatus(Long applicantId, String subject, String message);
    public List<JobApplication> sendEmailsToShortlisted(String subject, String message);
    public List<JobApplication> sendEmailsToRejected(String subject, String message);
     // Single applicant email
    public void sendEmailToSingleApplicant(Long applicantId, String subject, String message);
    public void deletePayslip(Long id); 
    public SalaryResponseDto addSalary(SalaryRequestDto salRequestDto);
	public SalaryResponseDto getActiveSalary(Long employeeId);
	public List<SalaryResponseDto> getAllActiveSalaries();
	public SalaryResponseDto updateSalary(Long salaryId, SalaryRequestDto dto);
	public String deleteSalary(Long salaryId); 
	public List<EmployeeLeaveDTO> leaves(ApprovalStatus status);
	public EmployeeLeaveDTO approvalOrReject(Long leaveId, ApprovalStatus status) ;
	public List<EmployeeMonthlyReportDTO> generateMontlyReport();
    public EmployeeMonthlyReportDTO getMonthlyReportOfEmployee(Long employeeId, Integer year, Integer month);
	public List<EmployeeLeaveDTO> employeeOnLeave();
	public String disableEmp(Long id);
	public String enableEmp(Long id);

}
