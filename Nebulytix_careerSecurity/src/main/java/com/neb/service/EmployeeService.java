
package com.neb.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import com.neb.dto.AddDailyReportRequestDto;
import com.neb.dto.EmployeeDTO;
import com.neb.dto.EmployeeLeaveDTO;
import com.neb.dto.EmployeeRegulationDTO;
import com.neb.dto.ResponseMessage;
import com.neb.dto.WorkResponseDto;
import com.neb.dto.employee.AddEmployeeRequest;
import com.neb.dto.employee.EmployeeProfileDto;
import com.neb.dto.employee.UpdateEmployeeRequestDto;
import com.neb.dto.employee.UpdateEmployeeResponseDto;
import com.neb.entity.Employee;
import com.neb.entity.Payslip;
import com.neb.entity.Users;
import com.neb.entity.Work;

public interface EmployeeService {

	public Long createEmployee(AddEmployeeRequest empReq, Users user);
	public EmployeeProfileDto getMyProfile();
    public Payslip generatePayslip(Long employeeId, String monthYear) throws Exception;
    public Employee getEmployeeById(Long id);
    public List<Work> getTasksByEmployee(Long employeeId);
    public WorkResponseDto submitReport(Long taskId, String status, String reportDetails, MultipartFile reportAttachment, LocalDate submittedDate);
    public String submitDailyReport(AddDailyReportRequestDto request);
    public String saveProfilePictureUrl(Long employeeId, String imageUrl);
    public boolean deleteProfilePicture(Long employeeId);
	public UpdateEmployeeResponseDto updateEmployee(Long employeeId, UpdateEmployeeRequestDto requestDto);
	public EmployeeDTO webClockin( Long employeeId) ;
	public EmployeeDTO webClockout(Long employeeId);
	public EmployeeLeaveDTO applyLeave(EmployeeLeaveDTO dto);
    public EmployeeLeaveDTO applyWFH(EmployeeLeaveDTO wfh);
    public String regularize(EmployeeRegulationDTO regulation);
	String getPayslipUrl(Long payslipId);

}
