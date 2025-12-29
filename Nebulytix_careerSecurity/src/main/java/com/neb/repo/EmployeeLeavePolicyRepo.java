package com.neb.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neb.entity.LeavePolicy;



public interface EmployeeLeavePolicyRepo extends JpaRepository<LeavePolicy, Long> {

}
