package com.neb.dto.user;

import java.util.Set;

import com.neb.constants.Role;

import lombok.Data;

@Data
public class UserDto {
	
	private String email;
	private String password;
	private Set<Role> roles;
}
