
package com.neb.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.neb.dto.*;
import com.neb.dto.employee.AddEmployeeRequest;
import com.neb.entity.*;

public interface EmployeeService {

	public Long createEmployee(AddEmployeeRequest empReq, Users user);
    // Generate payslip for a specific employee and month
    public Payslip generatePayslip(Long employeeId, String monthYear) throws Exception;
    
    public Boolean addEmployee(AddEmployeeRequestDto addEmpReq,Long userId,String jobRole);

    // Get employee details by ID
    public Employee getEmployeeById(Long id);

    // Get all tasks assigned to an employee
    public List<Work> getTasksByEmployee(Long employeeId);

    // Submit task report after completion
    public WorkResponseDto submitReport(Long taskId, String status, String reportDetails, MultipartFile reportAttachment, LocalDate submittedDate);

    // Get employee details by email
    public EmployeeDetailsResponseDto getEmployeeByEmail(String email);
    
    public String submitDailyReport(AddDailyReportRequestDto request);
    
    public String uploadProfilePicture(Long employeeId, MultipartFile file);
    
    boolean deleteProfilePicture(Long employeeId);

}
