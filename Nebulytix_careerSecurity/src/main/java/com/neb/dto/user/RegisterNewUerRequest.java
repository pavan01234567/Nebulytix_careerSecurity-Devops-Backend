package com.neb.dto.user;

import com.neb.dto.employee.AddEmployeeRequest;

import lombok.Data;

@Data
public class RegisterNewUerRequest {

	private UserDto userDto;
	private AddEmployeeRequest empReq;
}
