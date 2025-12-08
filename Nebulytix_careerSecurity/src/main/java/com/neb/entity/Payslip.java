/**
 * --------------------------------------------------------------
 * Purpose :
 *   Represents an employee’s monthly payslip details in the system.
 *
 * Description :
 *   - Stores salary components, deductions, and net salary details.
 *   - Links each payslip to an employee using a Many-to-One relationship.
 *   - Also includes file details like payslip PDF name and path.
 *
 * Key Fields :
 *   ✅ payslipMonth      → The month and year of the payslip (e.g., "August 2025")
 *   ✅ generatedDate     → Date when the payslip was created
 *   ✅ basic, hra, flexi → Salary components
 *   ✅ pfDeduction, profTaxDeduction → Deductions
 *   ✅ netSalary         → Final take-home amount
 *   ✅ employee          → The employee to whom this payslip belongs
 * --------------------------------------------------------------
 */

package com.neb.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "payslips")
@Data
public class Payslip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String payslipMonth;      // Example: "August 2025"
    private LocalDateTime generatedDate;  // Payslip generation date

    // Salary breakdown
    private Double basic;
    private Double hra;
    private Double flexi;
    private Double grossSalary;

    // Deductions
    private Double pfDeduction;
    private Double profTaxDeduction;
    private Double totalDeductions;

    private Double netSalary;         // Final take-home salary

    // Payslip file details
    private String pdfPath;
    private String fileName;

    // Additional properties
    private String location;
    private Double balance;
    private Double aggrgDeduction;
    private Double IncHdSalary;
    private Double taxCredit;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;        // Linked employee
}
