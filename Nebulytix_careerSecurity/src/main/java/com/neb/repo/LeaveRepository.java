package com.neb.repo;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neb.entity.Employee;
import com.neb.entity.Leave;

public interface LeaveRepository extends JpaRepository<Leave, Long> {

    List<Leave> findByEmployeeAndTypeAndStatus(
            Employee employee,
            Leave.LeaveType type,
            Leave.LeaveStatus status
    );
}
