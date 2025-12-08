
package com.neb.repo;

import java.util.List;
import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.neb.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    //Optional<Employee> findByEmailAndPasswordAndLoginRole(String email, String password, String loginRole);

    boolean existsByEmail(String email);
    
   // List<Employee> findByLoginRoleNot(String loginRole);

    //List<Employee> findByLoginRoleNotIn(List<String> roles);
    
    Optional<Employee> findByEmail(String email);
    //@Query("SELECT e FROM Employee e WHERE e.loginRole = 'hr'")
    //Page<Employee> findAllHrs(Pageable pageable);
    
    public Employee findByUserId(Long userId);
}
