package com.neb.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neb.entity.EmployeeBankDetails;

public interface EmployeeBankDetailsRepository
        extends JpaRepository<EmployeeBankDetails, Long> {

    Optional<EmployeeBankDetails> findByEmployeeId(Long employeeId);
}
