package com.neb.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity 
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeMonthlyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK → Employee (using business employeeId)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "employee_id",
        nullable = false
    )
    private Employee employee;
    
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
