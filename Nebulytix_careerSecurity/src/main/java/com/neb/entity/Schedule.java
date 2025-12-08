/**
 * --------------------------------------------------------------
 * Purpose :
 *   Represents a schedule or meeting assigned by Admin or HR.
 *
 * Description :
 *   - Stores scheduling information such as date, time, purpose, and status.
 *   - Linked to both Admin/HR (who created it) and Employee (who is scheduled).
 *   - Helps track upcoming, completed, or cancelled schedules.
 *
 * Relationships :
 *   ✅ Many schedules can be created by one Admin/HR.
 *   ✅ Many schedules can be assigned to one Employee.
 * --------------------------------------------------------------
 */

package com.neb.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "schedules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;             // Example: "Project Meeting", "Interview", "Training Session"
    private String description;       // Details about the event
    private LocalDateTime startTime;  // Start date and time
    private LocalDateTime endTime;    // End date and time
    private String location;          // Physical or virtual (Google Meet link, Zoom, etc.)
    private String status;            // UPCOMING, COMPLETED, CANCELLED


    // ✅ The Employee for whom this schedule is set
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    @JsonIgnoreProperties({"schedules"})
    private Employee employee;
}
