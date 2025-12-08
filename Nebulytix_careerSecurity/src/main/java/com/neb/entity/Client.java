package com.neb.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "clients")
@Data
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private Users user;

    private String companyName;
    private String contactPerson;
    private String contactEmail;
    private String phone;
    private String alternatePhone;

    private String address;
    private String website;
    private String industryType;     // IT, Finance, Manufacturing etc.
    private String gstNumber;

    private String status = "active"; // active, inactive, blacklisted

    private LocalDate createdDate = LocalDate.now();
    private LocalDate updatedDate;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Project> projects = new ArrayList<>();
}
