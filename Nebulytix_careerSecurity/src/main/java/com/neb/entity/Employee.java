
package com.neb.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "employees")
@Data
@SQLDelete(sql = "update employees set emp_status='inactive' where id=?")
@SQLRestriction("emp_status<> 'inactive'")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;


    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private String cardNumber;
    
    private String designation;  // e.g., Senior Java Developer, HR Executive new 
    private String department;   // e.g., HR, Java, Finance, QA new 

    private String gender;
    private LocalDate joiningDate;
    private Double salary;
    private int daysPresent;
    private int paidLeaves;
    
    private String profilePictureUrl;
    private String profilePicturePath;
    
    // Bank details
    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL)
    private EmployeeBankDetails bankDetails;
    
    private String empStatus = "active";
    
    // Projects where employee works
    @ManyToMany(mappedBy = "employees")
    private List<Project> assignedProjects = new ArrayList<>();

    // Projects managed by this employee (if ROLE_MANAGER)
    @OneToMany(mappedBy = "projectManager")
    private List<Project> managedProjects = new ArrayList<>();


    // One employee can have multiple work records
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<Work> works = new ArrayList<>();

    // One employee can have multiple payslips
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payslip> payslips = new ArrayList<>();
    
    public Employee() {}
    
    public Employee(Users user, String firstName, String lastName) {
        this.user = user;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
