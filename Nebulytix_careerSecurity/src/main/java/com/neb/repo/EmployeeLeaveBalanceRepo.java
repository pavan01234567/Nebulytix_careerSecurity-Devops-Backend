package com.neb.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.neb.entity.Employee;
import com.neb.entity.EmployeeLeaveBalance;
import com.neb.util.EmployeeLeaveType; // 👈 IMPORTANT: Correct import

public interface EmployeeLeaveBalanceRepo extends JpaRepository<EmployeeLeaveBalance, Long> {

    // Main method used for checking leave balance
    Optional<EmployeeLeaveBalance> findByEmployeeAndLeaveTypeAndCurrentYear(
            Employee employee, EmployeeLeaveType leaveType, Integer currentYear
    );

    // Allowed: Search by employeeId (same logic but by id)
    Optional<EmployeeLeaveBalance> findByEmployee_IdAndLeaveTypeAndCurrentYear(
            Long id, EmployeeLeaveType leaveType, Integer currentYear
    );

    // Get record directly
    EmployeeLeaveBalance findByLeaveTypeAndEmployee_Id(
            EmployeeLeaveType leaveType, Long id
    );
}
