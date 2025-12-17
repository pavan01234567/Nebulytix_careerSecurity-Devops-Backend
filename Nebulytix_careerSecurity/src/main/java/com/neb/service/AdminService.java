
package com.neb.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.neb.dto.*;
import com.neb.dto.client.ClientDto;
import com.neb.dto.user.AdminProfileDto;
import com.neb.dto.user.RegisterNewClientRequest;
import com.neb.dto.user.RegisterNewUerRequest;
import com.neb.dto.user.UserDto;
import com.neb.entity.Project;

public interface AdminService {

    // For retrieving all employee details
    //public List<EmployeeDetailsResponseDto> getEmployeeList();
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
  
    public byte[] generateDailyReport(LocalDate date)throws Exception;
    public EmployeeDetailsResponseDto updateHrDetails(Long id, UpdateEmployeeRequestDto dto);


	//public AddEmployeeResponseDto addAdmin(AddEmployeeRequestDto addEmpReq);

	public String deleteAdmin(Long id);
	
	public List<UserDto> getOnlyAdmin();
	
	List<ClientDto> getClientList();
//    Project addProject(AddProjectRequestDto req);
	public List<EmployeeDetailsResponseDto> getOnlyHr();
	public List<EmployeeDetailsResponseDto> getOnlyEmployee();

}
