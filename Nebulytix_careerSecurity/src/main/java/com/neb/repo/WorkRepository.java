/**
 * ---------------------------------------------------------------
 * File Name   : WorkRepository.java
 * Package     : com.neb.repository
 * ---------------------------------------------------------------
 * Purpose :
 *   Handles database operations related to employee work or tasks.
 *
 * Description :
 *   Extends JpaRepository to provide built-in CRUD operations.
 *    Defines custom methods to fetch work details assigned to employees.
 *
 * Custom Methods :
 *   ✅ findByEmployeeId(Long employeeId)
 *        → Fetches all tasks assigned to a specific employee by ID.
 *
 *   ✅ findByEmployee(Employee emp)
 *        → Fetches all tasks assigned to a specific Employee object.
 *
 * ---------------------------------------------------------------
 */

package com.neb.repo;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.neb.entity.Employee;
import com.neb.entity.Work;

public interface WorkRepository extends JpaRepository<Work, Long> {

    List<Work> findByEmployeeId(Long employeeId);
    List<Work> findByEmployee(Employee emp);
    List<Work> findBySubmittedDate(LocalDate submittedDate);
    
}
