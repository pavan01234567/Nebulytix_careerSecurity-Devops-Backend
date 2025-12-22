package com.neb.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neb.entity.PasswordResetOtp;

public interface PasswordResetOtpRepository 
        extends JpaRepository<PasswordResetOtp, Long> {

    Optional<PasswordResetOtp> findTopByEmailAndUsedFalseOrderByExpiryTimeDesc(String email);
    
}
