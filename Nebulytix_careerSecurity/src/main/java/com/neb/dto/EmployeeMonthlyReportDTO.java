package com.neb.dto;



import com.neb.entity.EmployeeMonthlyReport;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeMonthlyReportDTO {
	
	
	
    public EmployeeMonthlyReportDTO(EmployeeMonthlyReport report) {
		this.employeeId = report.getEmployee().getId();
		this.employeeName = report.getEmployee().getFirstName()+" "+report.getEmployee().getLastName();
		this.currentYear = report.getCurrentYear();
		this.currentMonth = report.getCurrentMonth();
		this.totalWorkingDays = report.getTotalWorkingDays();
		this.presentDays = report.getPresentDays();
		this.absentDays = report.getAbsentDays();
		this.leavesApplied = report.getLeavesApplied();
		this.wfhDays = report.getWfhDays();
	}
	private Long employeeId;
    private String employeeName;
   
    private Integer currentYear;
    private Integer currentMonth;

    // Attendance
    private Long totalWorkingDays;
    private Double presentDays;
    private Long absentDays;
   // private Long lateArrivals;
   // private Long totalWorkHours;

    // Leave summary
    private Long leavesApplied;
   // private Long leavesApproved;
   // private Long leavesRejected;
    private Long wfhDays;
}

