
package com.neb.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.neb.entity.Client;
import com.neb.entity.Employee;
import com.neb.entity.Project;
import com.neb.entity.Users;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

   public Optional<Employee> findByUserId(Long userId);
    
    @Query("""
    	    SELECT DISTINCT e
              FROM Employee e
              JOIN e.user u
              WHERE com.neb.constants.Role.ROLE_HR MEMBER OF u.roles
               AND com.neb.constants.Role.ROLE_ADMIN NOT MEMBER OF u.roles
              AND com.neb.constants.Role.ROLE_MANAGER NOT MEMBER OF u.roles
    	    """)
    public List<Employee> findOnlyHr();
    

    @Query("""
             SELECT DISTINCT e
               FROM Employee e
               JOIN e.user u
               WHERE com.neb.constants.Role.ROLE_EMPLOYEE MEMBER OF u.roles
               AND com.neb.constants.Role.ROLE_HR NOT MEMBER OF u.roles
               AND com.neb.constants.Role.ROLE_MANAGER NOT MEMBER OF u.roles
        """)
       public List<Employee> findOnlyEmployees();

    
    
    @Query("""
    		 SELECT DISTINCT u
             FROM Users u
             WHERE com.neb.constants.Role.ROLE_ADMIN MEMBER OF u.roles
              """)
   public List<Users> findOnlyAdmin();
    
    public List<Employee> findByProject_Id(Long projectId);
    
    @Query("""
    	    SELECT DISTINCT e
              FROM Employee e
              JOIN e.user u
              WHERE com.neb.constants.Role.ROLE_MANAGER MEMBER OF u.roles
              AND com.neb.constants.Role.ROLE_CLIENT NOT MEMBER OF u.roles
    	    """)
    public List<Employee> findOnlyManager();
   
    @Query("""
    	    SELECT DISTINCT c
    	    FROM Client c
    	    JOIN c.user u
    	    WHERE com.neb.constants.Role.ROLE_CLIENT MEMBER OF u.roles
    	      AND com.neb.constants.Role.ROLE_ADMIN NOT MEMBER OF u.roles
    	      AND com.neb.constants.Role.ROLE_HR NOT MEMBER OF u.roles
    	      AND com.neb.constants.Role.ROLE_MANAGER NOT MEMBER OF u.roles
    	""")
    public List<Client> findOnlyClients();

    
    @Query("SELECT e FROM Project p JOIN p.employees e WHERE p.id = :projectId")
    public  List<Employee> findEmployeesByProjectId(@Param("projectId") Long projectId);
    
    @Query("""
            SELECT p
            FROM Employee e
            JOIN e.project p
            WHERE e.id = :employeeId
        """)
     public  Project findProjectByEmployeeId(@Param("employeeId") Long employeeId);
      @Query("SELECT e FROM Employee e WHERE e.id = :id AND e.empStatus='inactive'")
     public Optional<Employee> findByIdIncludingInactive(@Param("id") Long id);

}
