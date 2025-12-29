package com.neb.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity
@RequiredArgsConstructor
@NoArgsConstructor
@Data
public class EmployeeLogInDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(length = 12)
	@NonNull
	private LocalDateTime loginTime;
	@NonNull
	private LocalDateTime logoutTime;
	@NonNull
	private String totalTime;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee_id",nullable = false)
	private Employee employee;
	@NonNull
	private String dayStatus;
	@NonNull
	private String arrivalTime;

}
