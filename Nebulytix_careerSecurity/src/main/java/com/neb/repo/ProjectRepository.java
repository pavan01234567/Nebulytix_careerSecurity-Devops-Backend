package com.neb.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.neb.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    // Get project list of that client
	List<Project> findByClient_Id(Long clientId);
    
	@Query("SELECT p FROM Project p WHERE p.client.id = :clientId")
	public List<Project> findProjectsByClientId(@Param("clientId") Long clientId);
	public  List<Project> findByClientId(Long clientId);
    @Query("""
		    SELECT p 
		    FROM Project p
		    JOIN p.employees e
		    WHERE e.id = :employeeId
		    """)
	public List<Project> findProjectsByEmployeeId(@Param("employeeId") Long employeeId);
    
    @Query("""
            SELECT DISTINCT p
            FROM Project p
            LEFT JOIN FETCH p.client
            LEFT JOIN FETCH p.employees
            WHERE p.id = :projectId
        """)
        Optional<Project> findProjectWithClientAndEmployees(
                @Param("projectId") Long projectId
        );
}
