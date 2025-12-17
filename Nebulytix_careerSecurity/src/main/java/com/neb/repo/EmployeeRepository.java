
package com.neb.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.neb.entity.Employee;
import com.neb.entity.Users;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

//    public boolean existsByEmail(String email);
//    public Optional<Employee> findByEmail(String email);
    Optional<Employee> findByUserId(Long userId);
   
    
    @Query("""
    	    SELECT DISTINCT e
              FROM Employee e
              JOIN e.user u
              WHERE com.neb.constants.Role.ROLE_HR MEMBER OF u.roles
              AND com.neb.constants.Role.ROLE_ADMIN NOT MEMBER OF u.roles
              AND com.neb.constants.Role.ROLE_MANAGER NOT MEMBER OF u.roles
    	    """)
    	List<Employee> findOnlyHr();
    
    @Query("""
    		  SELECT DISTINCT e
               FROM Employee e
               JOIN e.user u
               WHERE com.neb.constants.Role.ROLE_EMPLOYEE MEMBER OF u.roles
               AND com.neb.constants.Role.ROLE_HR NOT MEMBER OF u.roles
    	    """)
    	List<Employee> findOnlyEmployees();
    
    //new 
    @Query("""
    		 SELECT DISTINCT u
             FROM Users u
             WHERE com.neb.constants.Role.ROLE_ADMIN MEMBER OF u.roles
             AND com.neb.constants.Role.ROLE_HR NOT MEMBER OF u.roles
             AND com.neb.constants.Role.ROLE_EMPLOYEE NOT MEMBER OF u.roles
             AND com.neb.constants.Role.ROLE_MANAGER NOT MEMBER OF u.roles
             AND com.neb.constants.Role.ROLE_CLIENT NOT MEMBER OF u.roles
    		""")
    List<Users> findOnlyAdmin();
}
