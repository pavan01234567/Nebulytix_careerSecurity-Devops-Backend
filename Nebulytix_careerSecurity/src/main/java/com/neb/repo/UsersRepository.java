package com.neb.repo;


import org.springframework.data.jpa.repository.JpaRepository;

import com.neb.entity.Users;

public interface UsersRepository extends JpaRepository<Users, Long>{

	public Users findByEmail(String email);
    public boolean existsByEmail(String username);
}
