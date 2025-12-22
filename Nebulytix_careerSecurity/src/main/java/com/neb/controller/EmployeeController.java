package com.neb.controller;
//original

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.multipart.MultipartFile;

import com.neb.dto.AddDailyReportRequestDto;
import com.neb.dto.ApplyLeaveRequestDto;
import com.neb.dto.EmployeeDetailsResponseDto;
import com.neb.dto.GeneratePayslipRequest;
import com.neb.dto.LoginRequestDto;
import com.neb.dto.PayslipDto;
import com.neb.dto.ResponseMessage;
import com.neb.dto.WorkResponseDto;
import com.neb.dto.employee.EmployeeProfileDto;
import com.neb.entity.Employee;
import com.neb.entity.Leave;
import com.neb.entity.Payslip;
import com.neb.entity.Work;
import com.neb.service.EmployeeService;
import com.neb.service.LeaveService;

@RestController
@RequestMapping("/api/employee")
@CrossOrigin(origins = "http://localhost:5173")
public class EmployeeController {
	
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private LeaveService leaveService;
	
	
	@GetMapping("/me")
    public ResponseEntity<ResponseMessage<EmployeeProfileDto>> getMyProfile() {

        EmployeeProfileDto dto = employeeService.getMyProfile();

        return ResponseEntity.ok(
                new ResponseMessage<>(200, "SUCCESS", "Profile fetched successfully", dto)
        );
    }
	
	@PostMapping("/payslip/generate")
    public ResponseEntity<PayslipDto> generate(@RequestBody GeneratePayslipRequest request) throws Exception {
        System.out.println(request);
		Payslip p = employeeService.generatePayslip(request.getEmployeeId(), request.getMonthYear());
        PayslipDto dto = PayslipDto.fromEntity(p);
        return ResponseEntity.ok(dto);
    }
	
	 // Get employee details
    @GetMapping("/get/{id}")
    public ResponseMessage<Employee> getEmployee(@PathVariable Long id) {
        Employee emp = employeeService.getEmployeeById(id);
        return new ResponseMessage<>(HttpStatus.OK.value(), HttpStatus.OK.name(), "Employee fetched successfully", emp);
    }
    
//    @GetMapping("/details/{email}")
//    public ResponseEntity<ResponseMessage<EmployeeDetailsResponseDto>> getEmployeeByEmail(@PathVariable String email) {
//    	EmployeeDetailsResponseDto emp = employeeService.getEmployeeByEmail(email);	
//        if (emp == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(new ResponseMessage<>(404, "NOT_FOUND", "Employee not found"));
//        }
//        return ResponseEntity.ok(
//                new ResponseMessage<>(200, "OK", "Employee fetched successfully", emp)
//        );
//    }
    
    // Get tasks assigned to employee
    @GetMapping("/tasks/{employeeId}")
    public ResponseMessage<List<Work>> getTasks(@PathVariable Long employeeId) {
        List<Work> tasks = employeeService.getTasksByEmployee(employeeId);
        return new ResponseMessage<>(HttpStatus.OK.value(), HttpStatus.OK.name(), "Tasks fetched successfully", tasks);
    }
    
   // Submit task report
    @PutMapping("/task/submit/{taskId}")
    public ResponseEntity<ResponseMessage<WorkResponseDto>> submitTaskReport(
            @PathVariable Long taskId,
            @RequestParam("status") String status,
            @RequestParam("reportDetails") String reportDetails,
            @RequestParam(value = "reportAttachment", required = false) MultipartFile reportAttachment
    ) {
        WorkResponseDto updatedTask = employeeService.submitReport(taskId, status, reportDetails, reportAttachment, LocalDate.now());
        ResponseMessage<WorkResponseDto> response = new ResponseMessage<>(
                HttpStatus.OK.value(),
                HttpStatus.OK.name(),
                "Report submitted successfully",
                updatedTask
        );
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/dailyReport/submit")
   public ResponseEntity<ResponseMessage<String>> submitDailyReport(@RequestBody AddDailyReportRequestDto reportDetails){
	  
    	String submitDailyReportResponse = employeeService.submitDailyReport(reportDetails);
    	
    	return ResponseEntity.ok(new ResponseMessage<String>(HttpStatus.OK.value(), HttpStatus.OK.name(), "daily report result", submitDailyReportResponse));
   }
    @PostMapping("/logout")
    public ResponseEntity<ResponseMessage<String>> logout() {

        return ResponseEntity.ok(
                new ResponseMessage<>(
                        HttpStatus.OK.value(),
                        HttpStatus.OK.name(),
                        "Logout successful",
                        "Admin logged out successfully"
                )
        );
    }
    
    @PutMapping("/{id}/profile-picture")
    public ResponseEntity<ResponseMessage<String>> uploadProfilePicture(
            @PathVariable Long id,
            @RequestParam("profileImage") MultipartFile profileImage) {

        String imageUrl = employeeService.uploadProfilePicture(id, profileImage);
        return ResponseEntity.ok(new ResponseMessage<>(HttpStatus.OK.value(), HttpStatus.OK.name(),
                "Profile picture uploaded successfully", imageUrl));
    }
    @DeleteMapping("/{id}/profile-picture")
    public ResponseEntity<ResponseMessage<String>> deleteProfilePicture(@PathVariable Long id) {

        boolean deleted = employeeService.deleteProfilePicture(id);

        if (deleted) {
            return ResponseEntity.ok(
                    new ResponseMessage<>(
                            HttpStatus.OK.value(),
                            HttpStatus.OK.name(),
                            "Profile picture deleted successfully",
                            "Profile image removed from database and folder"
                    )
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseMessage<>(
                            HttpStatus.NOT_FOUND.value(),
                            HttpStatus.NOT_FOUND.name(),
                            "Profile picture not found",
                            null
                    ));
        }
    }
   

   
  
}
