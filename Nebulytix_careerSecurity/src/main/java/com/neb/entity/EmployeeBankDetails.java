package com.neb.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class EmployeeBankDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bankAccountNumber;
    private String ifscCode;
    private String bankName;
    private String pfNumber;
    private String panNumber;
    private String uanNumber;
    private String epsNumber;
    private String esiNumber;

    @OneToOne
    @JoinColumn(name = "employee_id", unique = true)
    private Employee employee;
}
