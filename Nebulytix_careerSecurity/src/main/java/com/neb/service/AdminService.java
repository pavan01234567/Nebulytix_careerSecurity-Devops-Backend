
package com.neb.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.neb.dto.AddWorkRequestDto;
import com.neb.dto.EmployeeDetailsResponseDto;
import com.neb.dto.WorkResponseDto;
import com.neb.dto.client.ClientDto;
import com.neb.dto.client.ClientProfileDto;
import com.neb.dto.client.UpdateClientRequest;
import com.neb.dto.employee.EmployeeProfileDto;
import com.neb.dto.employee.UpdateEmployeeRequestDto;
import com.neb.dto.user.AdminProfileDto;
import com.neb.dto.user.RegisterNewClientRequest;
import com.neb.dto.user.RegisterNewUerRequest;
import com.neb.dto.user.UserDto;

public interface AdminService {
	
	public Long createAdmin(UserDto userReq);
	public Long createEmployee(RegisterNewUerRequest empReq);
	public Long createClient(RegisterNewClientRequest clientReq);
	public AdminProfileDto getMyProfile();
	  // For assigning new work to an employee
	public String assignWork(AddWorkRequestDto request,MultipartFile file);
     // For fetching all assigned works to employee
    public List<WorkResponseDto> getAllWorks(Long empId);
     // For fetching work details of a specific employee
    public List<WorkResponseDto> getWorkByEmployee(Long empId);
     // Get employee details by ID
    public EmployeeDetailsResponseDto getEmployee(Long id);
    public String deleteHr(Long id);
    public String deleteClient(Long id);
    public String generateDailyReport(LocalDate date) throws Exception;
    public EmployeeDetailsResponseDto updateHrDetails(Long id, UpdateEmployeeRequestDto dto);
    public String deleteAdmin(Long id);
    public List<AdminProfileDto> getOnlyAdmin();
	public List<ClientDto> getClientList();
	public List<EmployeeProfileDto> getOnlyHr();
	public List<EmployeeProfileDto> getOnlyEmployee();
	public List<EmployeeProfileDto> getOnlyManager();
	public List<ClientProfileDto> getOnlyClient();
	public ClientProfileDto updateClient(Long clientId, UpdateClientRequest req);
	public String disableAdmin(Long id);
	public String enableAdmin(Long id);
	public String disableClient(Long id);
	public String enableClient(Long id);
	String saveProfilePictureUrl(Long employeeId, MultipartFile file);


	
}
