package com.neb.repo;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neb.entity.EmployeeSalary;

public interface EmployeeSalaryRepository extends JpaRepository<EmployeeSalary, Long> {
    public Optional<EmployeeSalary> findByEmployeeIdAndActiveTrue(Long employeeId);
    public List<EmployeeSalary> findByActiveTrue();
    List<EmployeeSalary> findByEmployeeIdOrderByEffectiveFromDesc(Long employeeId);
    Optional<EmployeeSalary> findByEmployee_IdAndActiveTrue(Long employeeId);
}
