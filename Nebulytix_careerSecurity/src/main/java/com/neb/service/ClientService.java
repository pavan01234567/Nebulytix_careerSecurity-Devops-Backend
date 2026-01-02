package com.neb.service;

import java.util.List;

import com.neb.dto.AddWorkRequestDto;
import com.neb.dto.WorkResponseDto;
import com.neb.dto.client.AddClientRequest;
import com.neb.dto.client.ClientProfileDto;
import com.neb.dto.employee.EmployeeProfileDto;
import com.neb.entity.DailyReport;
import com.neb.entity.Project;
import com.neb.entity.Users;

public interface ClientService {
	
    public ClientProfileDto getMyProfile();
	public Long createClient(AddClientRequest addClientReq, Users user);
    public List<Project> getProjectsForLoggedInClient();
    public String getProjectStatus(Long projectId);
    public List<EmployeeProfileDto> getEmployeesByProject(Long projectId);
    public WorkResponseDto assignWorkToEmployee(Long projectId, AddWorkRequestDto dto);
    public List<WorkResponseDto> getWorkByProject(Long projectId);
	public List<DailyReport> getReportsByProject(Long projectId);
}
