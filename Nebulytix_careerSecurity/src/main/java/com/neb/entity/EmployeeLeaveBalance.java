package com.neb.entity;

import com.neb.entity.Employee;
import com.neb.util.EmployeeLeaveType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.neb.util.EmployeeLeaveType;




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
    private EmployeeLeaveType leaveType;

    private Integer currentYear;

    private Long totalAllowed;
    private Long used;
    private Long remaining;
    
    
  

}
