package com.neb.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "attendances")
@Data
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    private LocalDate date;

    private LocalTime loginTime;

    private LocalTime logoutTime;

    @Enumerated(EnumType.STRING)
    private AttendanceStatus status;

    public enum AttendanceStatus {
        PRESENT,
        LEAVE,
        WFH,
        HALF_DAY
    }
}
