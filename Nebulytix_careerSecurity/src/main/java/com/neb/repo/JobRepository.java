package com.neb.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neb.entity.Job;

public interface JobRepository extends JpaRepository<Job, Long>{

}
