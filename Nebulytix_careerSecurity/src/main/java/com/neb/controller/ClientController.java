package com.neb.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neb.dto.AddWorkRequestDto;
import com.neb.dto.ResponseMessage;
import com.neb.dto.WorkResponseDto;
import com.neb.dto.client.ClientProfileDto;
import com.neb.entity.DailyReport;
import com.neb.entity.Employee;
import com.neb.entity.Project;
import com.neb.service.ClientService;

//@PreAuthorize("hasRole('CLIENT')")
@RestController
@RequestMapping("/api/client")
public class ClientController {

	@Autowired
	private ClientService clientService;
	
	@PreAuthorize("hasRole('CLIENT')")
	@GetMapping("/me")
    public ResponseEntity<ResponseMessage<ClientProfileDto>> getMyProfile() {

        ClientProfileDto dto = clientService.getMyProfile();

        return ResponseEntity.ok(
                new ResponseMessage<>(200, "SUCCESS", "Client profile fetched successfully", dto)
        );
    }
	 //  Get all projects for logged-in client
    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/projects")
    public ResponseEntity<ResponseMessage<List<Project>>> getProjectsForClient() {
        List<Project> projects = clientService.getProjectsForLoggedInClient();
        return ResponseEntity.ok(
                new ResponseMessage<>(200, "SUCCESS", "Client projects fetched successfully", projects)
        );
    }

    //  Get status of a specific project
    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/projects/{projectId}/status")
    public ResponseEntity<ResponseMessage<String>> getProjectStatus(@PathVariable Long projectId) {
        String status = clientService.getProjectStatus(projectId);
        return ResponseEntity.ok(
                new ResponseMessage<>(200, "SUCCESS", "Project status fetched successfully", status)
        );
    }

    //  Get employees working under a specific project
    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/projects/{projectId}/employees")
    public ResponseEntity<ResponseMessage<Optional<Employee>>> getEmployeesByProject(@PathVariable Long projectId) {
        Optional<Employee> employees = clientService.getEmployeesByProject(projectId);
        return ResponseEntity.ok(
                new ResponseMessage<>(200, "SUCCESS", "Employees for project fetched successfully", employees)
        );
    }

    //  Assign work/task to an employee under a project
    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/projects/{projectId}/assign-work")
    public ResponseEntity<ResponseMessage<WorkResponseDto>> assignWork(
            @PathVariable Long projectId,
            @RequestBody AddWorkRequestDto dto) {
        WorkResponseDto work = clientService.assignWorkToEmployee(projectId, dto);
        return ResponseEntity.ok(
                new ResponseMessage<>(200, "SUCCESS", "Work assigned successfully", work)
        );
    }

    //  Get all work/tasks under a project
    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<ResponseMessage<List<WorkResponseDto>>> getWorkByProject(@PathVariable Long projectId) {
        List<WorkResponseDto> works = clientService.getWorkByProject(projectId);
        return ResponseEntity.ok(
                new ResponseMessage<>(200, "SUCCESS", "Project work fetched successfully", works)
        );
    }
    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/projects/{projectId}/reports")
    public ResponseEntity<ResponseMessage<List<DailyReport>>> getProjectReports(@PathVariable Long projectId) {

        List<DailyReport> reports = clientService.getReportsByProject(projectId);

        return ResponseEntity.ok(
                new ResponseMessage<>(
                        200,
                        "SUCCESS",
                        "Project daily reports fetched successfully",
                        reports
                )
        );
}
}
