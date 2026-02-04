package com.neb.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

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
