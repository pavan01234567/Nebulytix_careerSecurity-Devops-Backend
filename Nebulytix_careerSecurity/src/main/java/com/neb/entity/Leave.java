package com.neb.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "leaves")
@Data
public class Leave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    private LocalDate fromDate;
    private LocalDate toDate;

    @Enumerated(EnumType.STRING)
    private LeaveType type;

    @Enumerated(EnumType.STRING)
    private LeaveStatus status;

    public enum LeaveType {
        SICK,
      CASUAL,
        WFH
   }
//
    public enum LeaveStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
}
