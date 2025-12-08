package com.neb.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.neb.entity.JobApplication;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long>{

	boolean existsByEmailIgnoreCase(String email);
	Optional<JobApplication> findByEmailIgnoreCase(String email);
	List<JobApplication> findByJob_Id(Long jobId);
	List<JobApplication> findByStatus(String status);
	@Query("SELECT j.email FROM JobApplication j WHERE j.id = :id")
	String findEmailByApplicationId(@Param("id") Long id);
}
