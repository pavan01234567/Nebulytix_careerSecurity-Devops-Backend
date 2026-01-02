
package com.neb.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;


import com.neb.dto.*;
import com.neb.dto.employee.AddEmployeeRequest;
import com.neb.dto.employee.EmployeeProfileDto;
import com.neb.dto.employee.UpdateEmployeeRequestDto;
import com.neb.dto.employee.UpdateEmployeeResponseDto;
import com.neb.entity.*;

public interface EmployeeService {

	public Long createEmployee(AddEmployeeRequest empReq, Users user);
	public EmployeeProfileDto getMyProfile();
    // Generate payslip for a specific employee and month
    public Payslip generatePayslip(Long employeeId, String monthYear) throws Exception;
    // Get employee details by ID
    public Employee getEmployeeById(Long id);
     // Get all tasks assigned to an employee
    public List<Work> getTasksByEmployee(Long employeeId);
     // Submit task report after completion
    public WorkResponseDto submitReport(Long taskId, String status, String reportDetails, MultipartFile reportAttachment, LocalDate submittedDate);
    public String submitDailyReport(AddDailyReportRequestDto request);
    public String uploadProfilePicture(Long employeeId, MultipartFile file);
    public boolean deleteProfilePicture(Long employeeId);
	public UpdateEmployeeResponseDto updateEmployee(Long employeeId, UpdateEmployeeRequestDto requestDto);
    public EmployeeDTO login(Long employeeId);
    public EmployeeDTO logout(Long employeeId);
    public EmployeeLeaveDTO applyLeave(EmployeeLeaveDTO empLeaveDto);
    public EmployeeLeaveDTO applyWFH(EmployeeLeaveDTO wfh);
    

}
