
package com.neb.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "employees")
//@Data
@Getter
@Setter
@SQLDelete(sql = "update employees set emp_status='inactive' where id=?")
@SQLRestriction("emp_status<> 'inactive'")
@ToString(exclude = {"employeeSessions","leaves","leaveBalance","montlyReport"})
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;


    private String firstName;
    private String lastName;
    //private String email;
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    
    public Employee() {}
    
    public Employee(Users user, String firstName, String lastName) {
        this.user = user;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private EmployeeBankDetails bankDetails;
    
//    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
//	private List<EmployeeLogInDetails> employeeSessions;
    
    
	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	private List<EmployeeLogInDetails> employeeSessions;
	

	@OneToMany(mappedBy = "employee",cascade = CascadeType.ALL,fetch=FetchType.LAZY)
	@JsonIgnore
	private List<EmployeeLeaves> leaves;
	
	@OneToMany(mappedBy = "employee",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	@JsonIgnore
	private List<EmployeeLeaveBalance> leaveBalance;
	
	@OneToMany(mappedBy = "employee",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	@JsonIgnore
	private List<EmployeeMonthlyReport> montlyReport;

}
