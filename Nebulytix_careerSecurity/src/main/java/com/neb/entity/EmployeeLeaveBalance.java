package com.neb.entity;
//
//import com.neb.entity.Employee;
//import com.neb.util.EmployeeLeaveType;
//import jakarta.persistence.*;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import com.neb.util.EmployeeLeaveType;
//
//
//
//
//@Entity
//@Data
//@NoArgsConstructor
//@Table(
//    name = "employee_leave_balance",
//    uniqueConstraints = @UniqueConstraint(
//         columnNames = {"employee_id", "leave_type", "current_year"}
//    )
//)
//public class EmployeeLeaveBalance {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "employee_id", nullable = false)
//    private Employee employee;
//
//    @Enumerated(EnumType.STRING)
//    private EmployeeLeaveType leaveType;
//
//    private Integer currentYear;
//
//    private Long totalAllowed;
//    private Long used;
//    private Long remaining;
// 
//}



import com.neb.util.EmployeeLeaveType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(
    name = "employee_leave_balance",
    uniqueConstraints = @UniqueConstraint(
         columnNames = {"employee_id", "leave_type", "current_year"}
    )
)
public class EmployeeLeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)   // MUST ADD THIS
    private EmployeeLeaveType leaveType;

    private Integer currentYear;
    private Long totalAllowed;
    private Long used;
    private Long remaining;
}

