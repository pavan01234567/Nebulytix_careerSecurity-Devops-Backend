package com.neb.repo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neb.entity.Employee;
import com.neb.entity.EmployeeLeaves;
import com.neb.util.ApprovalStatus;
import com.neb.util.EmployeeLeaveType;






public interface EmployeeLeaveRepository extends JpaRepository<EmployeeLeaves, Long>
{

	List<EmployeeLeaves> findByLeaveStatus(ApprovalStatus status);
	
//	Optional<EmployeeLeaves> findByEmployee_EmployeeIdAndLeaveType(Long id,EmployeeLeaveType type);
	Optional<EmployeeLeaves> findByEmployeeIdAndLeaveType(Long id, EmployeeLeaveType type);

	List<EmployeeLeaves> findByEmployeeAndLeaveStatus(Employee employe,ApprovalStatus status);
	
	
	List<EmployeeLeaves> findByEmployeeAndLeaveTypeAndLeaveStatus(Employee emp,EmployeeLeaveType earned,ApprovalStatus status);
	List<EmployeeLeaves> findByAppliedDateAndCurrentYearAndCurrentMonth(LocalDate date,Integer year,Integer month);
}
