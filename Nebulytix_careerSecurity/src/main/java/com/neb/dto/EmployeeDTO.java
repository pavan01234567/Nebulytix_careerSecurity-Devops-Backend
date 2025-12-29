package com.neb.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDTO {
	
	private Long id;
	private String firstName;
	private String lastName;
	private String displayName;
	private String duration;
	private LocalDateTime loginTime;
	private LocalDateTime logoutTime;
	private String empStatus;

	private String dayStatus;
	private String arrivalTime;

}
