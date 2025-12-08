package com.neb.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neb.entity.RefreshToken;
import com.neb.entity.Users;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    public Optional<RefreshToken> findByToken(String token);
    public int deleteByUser(Users user);
}
