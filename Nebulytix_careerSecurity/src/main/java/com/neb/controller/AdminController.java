//admin controller
package com.neb.controller;

import java.time.LocalDate;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import com.neb.dto.EmployeeDetailsResponseDto;
import com.neb.dto.GeneratePayslipRequest;
import com.neb.dto.PayslipDto;
import com.neb.dto.ProjectResponseDto;
import com.neb.dto.ResponseMessage;
import com.neb.dto.UpdateEmployeeRequestDto;
import com.neb.dto.UpdateProjectRequestDto;
import com.neb.dto.WorkResponseDto;
import com.neb.dto.client.ClientDto;
import com.neb.dto.client.ClientProfileDto;
import com.neb.dto.employee.EmployeeProfileDto;
import com.neb.dto.project.AddProjectRequestDto;
import com.neb.dto.user.AdminProfileDto;
import com.neb.dto.user.RegisterNewClientRequest;
import com.neb.dto.user.RegisterNewUerRequest;
import com.neb.dto.user.UserDto;
import com.neb.entity.Payslip;
import com.neb.entity.Project;

import com.neb.service.AdminService;
import com.neb.service.EmployeeService;
import com.neb.service.HrService;
import com.neb.service.ProjectService;



@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {

	@Autowired
	private AdminService adminService;
	
	@Autowired
	private HrService hrService;
	
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private ProjectService projectService;
	
    @PostMapping("/create-admin")
    public ResponseEntity<ResponseMessage> createAdmin(@RequestBody UserDto dto) {
        adminService.createAdmin(dto);
        return ResponseEntity.ok(
                new ResponseMessage(200, "OK", "Admin created successfully")
        );
    }

    @PostMapping("/create-employee")
    public ResponseEntity<ResponseMessage> createEmployee(@RequestBody RegisterNewUerRequest req) {

        adminService.createEmployee(req);

        return ResponseEntity.ok(
            new ResponseMessage(200, "OK", "User created successfully")
        );
    }

    @PostMapping("/create-client")
    public ResponseEntity<ResponseMessage> createClient(@RequestBody RegisterNewClientRequest req) {
        adminService.createClient(req);
        return ResponseEntity.ok(
                new ResponseMessage(200, "OK", "Client created successfully")
        );
    }
    
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/me")
    public ResponseEntity<ResponseMessage<AdminProfileDto>> getMyProfile() {

        AdminProfileDto dto = adminService.getMyProfile();

        return ResponseEntity.ok(
                new ResponseMessage<>(200, "SUCCESS", "Admin profile fetched", dto)
        );
    }
	    
	    // ✅ Get all Work of employee
	    @GetMapping("/getAllWork/{empId}")
	    public ResponseEntity<ResponseMessage<List<WorkResponseDto>>> getAllWork(@PathVariable Long empId) {
	        List<WorkResponseDto> works = adminService.getAllWorks(empId);
	        return ResponseEntity.ok(new ResponseMessage<>(200, "OK", "All work fetched successfully", works));
	    }
	    
	    //  Get Employee Work
	    @GetMapping("/getWork/{empId}")
	    public ResponseEntity<ResponseMessage<List<WorkResponseDto>>> getWorkByEmployee(@PathVariable Long empId) {
	        List<WorkResponseDto> works = adminService.getWorkByEmployee(empId);
	        return ResponseEntity.ok(new ResponseMessage<>(200, "OK", "Work fetched for employee", works));
	    }
	    // delete  admin by id 
	    @DeleteMapping("/delete/admin/{id}")//http://localhost:5054/api/admin/delete/admin/3
	    public ResponseEntity<ResponseMessage<?>> deleteAdmin(@PathVariable Long id){
	    	String deleteRes = adminService.deleteAdmin(id);
	    	return ResponseEntity.ok(new ResponseMessage<>(200, "OK", "Admin deleted successfully", deleteRes));
	    }
	    // delete hr by id
	    @DeleteMapping("/delete/hr/{id}")//http://localhost:5054/api/admin/delete/hr/3
	    public ResponseEntity<ResponseMessage<?>> deleteHr(@PathVariable Long id){
	    	String deleteRes = adminService.deleteHr(id);
	    	return ResponseEntity.ok(new ResponseMessage<>(200, "OK", "hr deleted successfully", deleteRes));
	    }
	    
	    @GetMapping("/getEmp/{id}")
		public ResponseEntity<ResponseMessage<EmployeeDetailsResponseDto>> getEmployee(@PathVariable Long id){
			
			EmployeeDetailsResponseDto employee = adminService.getEmployee(id);
		
			
			return ResponseEntity.ok(new ResponseMessage<EmployeeDetailsResponseDto>(HttpStatus.OK.value(), HttpStatus.OK.name(), " Employee fetched successfully", employee));
		}
		
		@GetMapping("/payslip/{id}/download")
	    public ResponseEntity<byte[]> download(@PathVariable Long id) throws Exception {
	        byte[] pdf = hrService.downloadPayslip(id);

	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_PDF);
	        headers.setContentDisposition(ContentDisposition
	            .attachment()
	            .filename("payslip_" + id + ".pdf")
	            .build());

	        return ResponseEntity.ok()
	                             .headers(headers)
	                             .body(pdf);
	    }

	    
	    @GetMapping("/payslip/{employeeId}")
	    public ResponseEntity<List<PayslipDto>> listPayslips(@PathVariable Long employeeId) {
	        List<PayslipDto> payslips = hrService.listPayslipsForEmployee(employeeId);
	        return ResponseEntity.ok(payslips);
	    }
	    
	    
	    @PostMapping("/payslip/generate")
	    public ResponseEntity<PayslipDto> generate(@RequestBody GeneratePayslipRequest request) throws Exception {
	        Payslip p = employeeService.generatePayslip(request.getEmployeeId(), request.getMonthYear());
	        PayslipDto dto = PayslipDto.fromEntity(p);
	        return ResponseEntity.ok(dto);
	    }
		
	    @PutMapping("/editEmp/{empId}/{days}")
	    public ResponseEntity<ResponseMessage<EmployeeDetailsResponseDto>> addAttendence(@PathVariable Long empId, @PathVariable int days){
	    	
	    	EmployeeDetailsResponseDto updatedEmp = hrService.addAttendence(empId, days);
	    	
	    	return ResponseEntity.ok(new ResponseMessage<EmployeeDetailsResponseDto>(HttpStatus.OK.value(), HttpStatus.OK.name(), "employee details updated", updatedEmp));
	    }
	    
	    @GetMapping("/reports/daily")
	    public ResponseEntity<byte[]> generateReport() throws Exception {
	    		    	
	    	LocalDate date = LocalDate.of(2025, 11, 05);
	        byte[] pdfBytes = adminService.generateDailyReport(date);
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_PDF);
	        headers.setContentDisposition(ContentDisposition
	            .attachment()
	            .filename("DailyReport_" + date + ".pdf")
	            .build());
	        System.out.println("pdf generated");
	        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
	    }

	 @PutMapping("/update/hr/{id}")
	 public ResponseEntity<ResponseMessage<EmployeeDetailsResponseDto>> updateHrDetails(
	         @PathVariable Long id,
	         @RequestBody UpdateEmployeeRequestDto updateReq) {

	     EmployeeDetailsResponseDto updatedHr = adminService.updateHrDetails(id, updateReq);

	     return ResponseEntity.ok(
	         new ResponseMessage<>(
	             HttpStatus.OK.value(),
	             HttpStatus.OK.name(),
	             "HR details updated successfully",
	             updatedHr
	         )
	     );
	 } 
	 
	 // GET /api/employees/hr-employee
	    @GetMapping("fetch/admin")
	    public ResponseEntity<ResponseMessage<List<AdminProfileDto>>> getAllAdmin() {
	            List<AdminProfileDto> allAdmin = adminService.getOnlyAdmin();
	          return ResponseEntity.ok(
	              new ResponseMessage<>(200, "OK", "Admin fetched successfully", allAdmin)
	      );
	    }
	    
	    @GetMapping("fetch/manager")
	    public ResponseEntity<ResponseMessage<List<EmployeeProfileDto>>> getAllManager() {
	           List<EmployeeProfileDto> allManager = adminService.getOnlyManager();
    	        return ResponseEntity.ok(
	              new ResponseMessage<>(200, "OK", "Managers fetched successfully", allManager)
	      );
	    }
	    
	    // GET /api/admin/fetch/hr
	    @GetMapping("fetch/hr")
	    public ResponseEntity<ResponseMessage<List<EmployeeProfileDto>>> getAllHr() {
	           List<EmployeeProfileDto> allHr = adminService.getOnlyHr();
//	        return new ResponseMessage<>(HttpStatus.OK.value(), HttpStatus.OK.name(), "Employee and HR fetched successfully", employees);
	        return ResponseEntity.ok(
	              new ResponseMessage<>(200, "OK", "Hr's fetched successfully", allHr)
	      );
	    }
	   
	    @GetMapping("/clients")
	    public ResponseEntity<ResponseMessage<List<ClientDto>>> getClientList() {
	        List<ClientDto> clients = adminService.getClientList();
	        return ResponseEntity.ok(new ResponseMessage<>(200, "SUCCESS", "Client list fetched successfully", clients));
	    }


	    @PostMapping(
	            value = "/project/add",
	            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
	    )
	    public ResponseEntity<ResponseMessage<String>> addProject(

	            @RequestPart("data") AddProjectRequestDto dto,
	            @RequestPart("quotation") MultipartFile quotation,
	            @RequestPart("requirement") MultipartFile requirement,
	            @RequestPart("contract") MultipartFile contract,
	            @RequestPart(value = "documents", required = false)
	            List<MultipartFile> documents) {

	        projectService.addProject(dto, quotation, requirement, contract, documents);

	        return ResponseEntity.ok(
	                new ResponseMessage<>(
	                        200,
	                        "SUCCESS",
	                        "Project added successfully",
	                        null
	                )
	        );
	    }

	 // GET /api/admin/fetch/employee
	    @GetMapping("fetch/employee")
	    public ResponseEntity<ResponseMessage<List<EmployeeProfileDto>>> getAllEmployees() {
	           List<EmployeeProfileDto> allEmployee = adminService.getOnlyEmployee();
	          return ResponseEntity.ok(
	              new ResponseMessage<>(200, "OK", "Employees fetched successfully", allEmployee)
	      );
	    }
	    
	    @GetMapping("fetch/clients")
	    public ResponseEntity<ResponseMessage<List<ClientProfileDto>>> getAllClients() {
	          List<ClientProfileDto> allClient = adminService.getOnlyClient();
	          return ResponseEntity.ok(
	              new ResponseMessage<>(200, "OK", "Clients fetched successfully", allClient)
	      );
	    }
	    // GET All Projects
	    @GetMapping("/projects")
	    @PreAuthorize("hasRole('ROLE_ADMIN')")
	    public ResponseEntity<ResponseMessage<List<ProjectResponseDto>>> getAllProjects() {
	        return ResponseEntity.ok(projectService.getAllProjects());
	    }

	    // GET Project By ID
	    @GetMapping("/{projectId}")
	    public ResponseEntity<ResponseMessage<ProjectResponseDto>> getProject(@PathVariable Long projectId) {
	        return ResponseEntity.ok(projectService.getProjectById(projectId));
	    }

	    // UPDATE Project
	    @PutMapping("/{projectId}")
	    public ResponseEntity<ResponseMessage<ProjectResponseDto>> updateProject(
	            @PathVariable Long projectId,
	            @RequestBody UpdateProjectRequestDto dto) {
	        return ResponseEntity.ok(projectService.updateProject(projectId, dto));
	    }

	    // DELETE Project
	    @PreAuthorize("hasRole('ROLE_ADMIN')")
	    @DeleteMapping("/{projectId}")
	    public ResponseEntity<ResponseMessage<String>> deleteProject(@PathVariable Long projectId) {
	        return ResponseEntity.ok(projectService.deleteProject(projectId));
	    }
	    
	    @PreAuthorize("hasRole('ADMIN')")
	    @PutMapping("/{projectId}/status")
	    public ResponseEntity<ProjectResponseDto> updateProjectStatus(
	            @PathVariable Long projectId,
	            @RequestParam String status) {

	        ProjectResponseDto updatedProject = projectService.updateProjectStatus(projectId, status);
	        return ResponseEntity.ok(updatedProject);
	    }
	    
	    @GetMapping("/projects/client/{clientId}")
	    public ResponseEntity<ResponseMessage<List<ProjectResponseDto>>> getProjectsByClient(
	            @PathVariable Long clientId) {

	        List<ProjectResponseDto> projects = projectService.getProjectsByClient(clientId);

	        return ResponseEntity.ok(
	                new ResponseMessage<>(
	                        HttpStatus.OK.value(),
	                        HttpStatus.OK.name(),
	                        "Projects fetched successfully for client",
	                        projects
	                )
	        );
	    }
}
