/**
 * --------------------------------------------------------------
 * Purpose :
 *   Represents a work task or assignment given to an employee.
 *
 * Description :
 *   - Stores task details like title, description, assigned and due dates.
 *   - Tracks the task status using the WorkStatus enum 
 *     (ASSIGNED, IN_PROGRESS, COMPLETED, REPORTED).
 *   - Keeps report details when an employee submits their work.
 *   - Each work record is linked to a specific employee.
 *
 * Key Fields :
 *   ✅ title, description   → Task information
 *   ✅ assignedDate, dueDate → Task timeline
 *   ✅ status               → Current task status
 *   ✅ reportDetails         → Employee’s submitted report
 *   ✅ employee              → Employee assigned to this task
 * --------------------------------------------------------------
 */

package com.neb.entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.neb.constants.WorkStatus;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "work")
@Data
public class Work {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Task Details
    private String title;
    private String description;
    private LocalDate assignedDate;
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private WorkStatus status; // ASSIGNED, IN_PROGRESS, COMPLETED

    // Report Details
    private String reportDetails;
    private LocalDate submittedDate;
    private String reportAttachmentUrl;
   
    // Relation with Employee
    @ManyToOne
    @JoinColumn(name = "employee_id")
    @JsonIgnore
    private Employee employee;
    
    private String attachmentUrl;
}
