package com.neb.repo;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neb.entity.EmployeeSalary;

public interface EmployeeSalaryRepository extends JpaRepository<EmployeeSalary, Long> {

    Optional<EmployeeSalary> findByEmployeeIdAndActiveTrue(Long employeeId);

	List<EmployeeSalary> findByActiveTrue();
}
