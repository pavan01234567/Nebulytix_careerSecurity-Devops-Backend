package com.neb.controller;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.springframework.web.bind.annotation.RestController;

import com.neb.constants.Role;
import com.neb.dto.AddEmployeeRequestDto;
import com.neb.dto.AddEmployeeResponseDto;
import com.neb.dto.AddJobRequestDto;
import com.neb.dto.EmailRequestDto;
import com.neb.dto.EmployeeDetailsResponseDto;
import com.neb.dto.GeneratePayslipRequest;
import com.neb.dto.JobDetailsDto;
import com.neb.dto.LoginRequestDto;
import com.neb.dto.PayslipDto;
import com.neb.dto.ResponseMessage;
import com.neb.dto.UpdateEmployeeRequestDto;
import com.neb.dto.UpdatePasswordRequestDto;
import com.neb.dto.employee.EmployeeProfileDto;
import com.neb.dto.user.RegisterNewUerRequest;
import com.neb.dto.user.UserDto;
import com.neb.entity.Attendance;
import com.neb.entity.JobApplication;
import com.neb.entity.Leave;
import com.neb.entity.Payslip;
import com.neb.service.AdminService;
import com.neb.service.EmployeeService;
import com.neb.service.HrService;
import com.neb.service.LeaveService;
import com.neb.service.UsersService;

@RestController
@RequestMapping("/api/hr")
@CrossOrigin(origins = "http://localhost:5173")
public class HrController {

    @Autowired
    private HrService service;

    @Autowired
    private EmployeeService employeeService;
    
    @Autowired
    private UsersService usersService;
    
    @Autowired
    private AdminService adminService;
    @Autowired
	private LeaveService leaveService;
    
    @GetMapping("/me")
    public ResponseEntity<ResponseMessage<EmployeeProfileDto>> getMyProfile() {

        EmployeeProfileDto dto = employeeService.getMyProfile();

        return ResponseEntity.ok(
                new ResponseMessage<>(200, "SUCCESS", "HR profile fetched successfully", dto)
        );
    }
    
    @PostMapping("/create-employee")
    public ResponseEntity<ResponseMessage> createEmployee(@RequestBody RegisterNewUerRequest req) {

        adminService.createEmployee(req);

        return ResponseEntity.ok(
            new ResponseMessage(200, "OK", "User created successfully")
        );
    }

//    @GetMapping("/getEmpList")
//    public ResponseEntity<ResponseMessage<List<EmployeeDetailsResponseDto>>> getEmployeeList() {
//        List<EmployeeDetailsResponseDto> employeeList = service.getEmployeeList();
//        return ResponseEntity.ok(new ResponseMessage<>(HttpStatus.OK.value(), HttpStatus.OK.name(), "All Employee fetched successfully", employeeList));
//    }
    
//    @PostMapping("/addEmployee")
//	public ResponseEntity<String> addEmployee(@RequestBody RegisterNewUerRequest addEmpReq){
//		
//		UserDto userDto= addEmpReq.getUserDto();
//		
//		Set<Role> roles = new HashSet<Role>();
//		roles.add(Role.ROLE_EMPLOYEE);
//		
//		Long id = usersService.saveUser(userDto,roles);
//		
//		if(id!=null) {
//			Boolean empStatus = employeeService.addEmployee(addEmpReq.getEmpReq(), id,"EMPLOYEE");
//			
//			if (empStatus) {
//		        return ResponseEntity.status(HttpStatus.CREATED).body("Employee Created Successfully");
//		    }	
//			else {
//				usersService.deleteUser(id);
//				return new ResponseEntity<>("failed", HttpStatus.INTERNAL_SERVER_ERROR);
//			}
//		}
//		return new ResponseEntity<>("failed", HttpStatus.INTERNAL_SERVER_ERROR);
//	}
//	
    
   
    @GetMapping("/getEmp/{id}")
    public ResponseEntity<ResponseMessage<EmployeeDetailsResponseDto>> getEmployee(@PathVariable Long id) {
        EmployeeDetailsResponseDto employee = service.getEmployee(id);
        return ResponseEntity.ok(new ResponseMessage<>(HttpStatus.OK.value(), HttpStatus.OK.name(), "Employee fetched successfully", employee));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseMessage<String>> deleteEmployee(@PathVariable Long id) {
        String deleteById = service.deleteById(id);
        return ResponseEntity.ok(new ResponseMessage<>(HttpStatus.OK.value(), HttpStatus.OK.name(), "Employee deleted successfully", deleteById));
    }

    @GetMapping("/payslip/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable Long id) throws Exception {
        byte[] pdf = service.downloadPayslip(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename("payslip_" + id + ".pdf").build());
        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    @GetMapping("/payslip/{employeeId}")
    public ResponseEntity<List<PayslipDto>> listPayslips(@PathVariable Long employeeId) {
        List<PayslipDto> payslips = service.listPayslipsForEmployee(employeeId);
        return ResponseEntity.ok(payslips);
    }

    @PostMapping("/payslip/generate")
    public ResponseEntity<PayslipDto> generate(@RequestBody GeneratePayslipRequest request) throws Exception {
        Payslip p = employeeService.generatePayslip(request.getEmployeeId(), request.getMonthYear());
        PayslipDto dto = PayslipDto.fromEntity(p);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/editEmp/{empId}/{days}")
    public ResponseEntity<ResponseMessage<EmployeeDetailsResponseDto>> addAttendence(@PathVariable Long empId, @PathVariable int days) {
        EmployeeDetailsResponseDto updatedEmp = service.addAttendence(empId, days);
        return ResponseEntity.ok(new ResponseMessage<>(HttpStatus.OK.value(), HttpStatus.OK.name(), "Employee details updated", updatedEmp));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseMessage<EmployeeDetailsResponseDto>> updateEmployee(@PathVariable Long id, @RequestBody UpdateEmployeeRequestDto updateReq) {
        EmployeeDetailsResponseDto updatedEmp = service.updateEmployee(id, updateReq);
        return ResponseEntity.ok(new ResponseMessage<>(HttpStatus.OK.value(), HttpStatus.OK.name(), "Employee details updated successfully", updatedEmp));
    }

    @PostMapping("/addJob")
    public ResponseEntity<ResponseMessage<JobDetailsDto>> addJob(@RequestBody AddJobRequestDto jobRequest) {
        JobDetailsDto jobRes = service.addJob(jobRequest);
        return ResponseEntity.ok(new ResponseMessage<>(HttpStatus.OK.value(), HttpStatus.OK.name(), "Job added successfully", jobRes));
    }

    @GetMapping("/allJobs")
    public ResponseEntity<ResponseMessage<List<JobDetailsDto>>> getJobList() {
        List<JobDetailsDto> allJobs = service.getAllJobs();
        return ResponseEntity.ok(new ResponseMessage<>(HttpStatus.OK.value(), HttpStatus.OK.name(), "All jobs fetched successfully", allJobs));
    }

    @PostMapping("/dailyReport/generate")
    public ResponseEntity<ResponseMessage<String>> generateDailyReport() {
        LocalDate d = LocalDate.now();
        String fileUrlOrMsg = service.generateDailyReport(d);
        if (fileUrlOrMsg == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage<>(500, "ERROR", "Failed to generate report", null));
        }
        if (fileUrlOrMsg.startsWith("/reports/")) {
            return ResponseEntity.ok(new ResponseMessage<>(200, "OK", "Report generated", fileUrlOrMsg));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage<>(404, "NOT_FOUND", fileUrlOrMsg, null));
        }
    }

    @GetMapping("/dailyReport/url")
    public ResponseEntity<ResponseMessage<String>> getDailyReportUrl() {
        LocalDate dt = LocalDate.now();
        String fullUrl = service.getDailyReportUrl(dt);
        if (fullUrl == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage<>(404, "NOT_FOUND", "No report for date: " + dt, null));
        }
        return ResponseEntity.ok(new ResponseMessage<>(200, "OK", "Report URL fetched", fullUrl));
    }

    // ============================ NEW / UPDATED ENDPOINTS ============================

    // Update applicant status
    @PutMapping("/job/updateStatus/{applicationId}/{status}")
    public ResponseEntity<ResponseMessage<String>> updateJobStatus(@PathVariable Long applicationId, @PathVariable Boolean status) {
        service.updateJobApplicationStatus(applicationId, status);
        String msg = status ? "Applicant Shortlisted" : "Applicant Rejected";
        return ResponseEntity.ok(new ResponseMessage<>(200, "OK", msg, null));
    }

 // Send email to all shortlisted applicants
    @PostMapping("/job/sendShortlistedEmails")
    public ResponseEntity<ResponseMessage<List<JobApplication>>> sendShortlistedEmails(@RequestBody EmailRequestDto emailRequest) {
        List<JobApplication> updatedApplicants = service.sendEmailsToShortlisted(emailRequest.getSubject(), emailRequest.getMessage());
        return ResponseEntity.ok(
            new ResponseMessage<>(200, "OK", "Emails sent to all shortlisted applicants and status updated", updatedApplicants)
        );
    }

    // Send email to all rejected applicants
    @PostMapping("/job/sendRejectedEmails")
    public ResponseEntity<ResponseMessage<List<JobApplication>>> sendRejectedEmails(@RequestBody EmailRequestDto emailRequest) {
        List<JobApplication> updatedApplicants = service.sendEmailsToRejected(emailRequest.getSubject(), emailRequest.getMessage());
        return ResponseEntity.ok(
            new ResponseMessage<>(200, "OK", "Emails sent to all rejected applicants and status updated", updatedApplicants)
        );
    }

    // Send individual invited email and update status
    @PostMapping("/job/sendInvitedEmail/{applicantId}")
    public ResponseEntity<ResponseMessage<String>> sendInvitedEmail(@PathVariable Long applicantId, @RequestBody EmailRequestDto emailRequest) {
        service.sendInvitedEmailAndUpdateStatus(applicantId, emailRequest.getSubject(), emailRequest.getMessage());
        return ResponseEntity.ok(new ResponseMessage<>(200, "OK", "Invited email sent and status updated to INVITED", null));
    }

    // Send individual rejected email and update status
    @PostMapping("/job/sendRejectedEmail/{applicantId}")
    public ResponseEntity<ResponseMessage<String>> sendRejectedEmail(@PathVariable Long applicantId, @RequestBody EmailRequestDto emailRequest) {
        service.sendRejectedEmailAndUpdateStatus(applicantId, emailRequest.getSubject(), emailRequest.getMessage());
        return ResponseEntity.ok(new ResponseMessage<>(200, "OK", "Rejected email sent and status updated to TERMINATED", null));
    }

    @DeleteMapping("/job/delete/{jobId}")
    public ResponseEntity<ResponseMessage<String>> deleteJob(@PathVariable Long jobId) {
        String result = service.deleteJob(jobId);
        return ResponseEntity.ok(new ResponseMessage<>(200, "OK", "Job deleted successfully", result));
    }

    @DeleteMapping("deletePaySlip/{id}")
    public ResponseEntity<ResponseMessage<String>> deletePayslip(@PathVariable Long id) {
        service.deletePayslip(id);
        return ResponseEntity.ok(new ResponseMessage<>(200, "OK", "PaySlip Deleted Successfully", null));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<ResponseMessage<String>> logout() {
        return ResponseEntity.ok(new ResponseMessage<>(HttpStatus.OK.value(), HttpStatus.OK.name(), "Logout successful", "Admin logged out successfully"));
    }
    
  


}
