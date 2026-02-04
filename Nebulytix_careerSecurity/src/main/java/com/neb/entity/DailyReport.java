package com.neb.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(
    name = "daily_reports",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"employee_id", "report_date"})
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;

    @Column(length = 4000)
    private String summary;

    // ✅ Cloudinary PDF URL
    @Column(name = "daily_report_url")
    private String dailyReportUrl;
}
