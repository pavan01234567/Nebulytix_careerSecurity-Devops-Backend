package com.neb.dto.user;

import java.util.Set;

import lombok.Data;

@Data
public class LoginResponse {

	private String accessToken;
	
    // Required for frontend authorization & menu visibility
    private Set<String> roles;

    // To decide which dashboard to load
    private String dashboard;
}
