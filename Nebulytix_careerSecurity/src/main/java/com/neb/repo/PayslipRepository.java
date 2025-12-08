/**
 * ---------------------------------------------------------------
 * File Name   : PayslipRepository.java
 * Package     : com.neb.repository
 * ---------------------------------------------------------------
 * Purpose :
 *   This interface handles database operations related to employee payslips.
 *
 * Description :
 *   - Extends JpaRepository to provide built-in CRUD operations 
 *     (save, findAll, findById, deleteById, etc.).
 *   - Adds a custom method to fetch all payslips for a specific employee.
 *
 * Custom Method :
 *   ✅ findByEmployeeId(Long employeeId)
 *        → Retrieves all payslips belonging to a given employee.
 *
 * Result :
 *   Simplifies payslip management and retrieval from the database.
 * ---------------------------------------------------------------
 */

package com.neb.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.neb.entity.Payslip;

public interface PayslipRepository extends JpaRepository<Payslip, Long> {

    List<Payslip> findByEmployeeId(Long employeeId);
}
