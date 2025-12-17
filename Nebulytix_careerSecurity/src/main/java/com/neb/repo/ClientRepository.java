package com.neb.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neb.entity.Client;
import com.neb.entity.Users;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByUserId(Long userId);

    // OR if you want to fetch by User entity itself
    Optional<Client> findByUser(Users user);
    
    
  
}