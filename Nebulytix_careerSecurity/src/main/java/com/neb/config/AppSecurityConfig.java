package com.neb.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.neb.filter.AppFilter;
import com.neb.service.UsersService;

import lombok.SneakyThrows;

@Configuration
@EnableWebSecurity
public class AppSecurityConfig {
	
	@Autowired
	private BCryptPasswordEncoder pwdEncoder;
	
	@Autowired
	private UsersService usersService;
	
	@Autowired
	private AppFilter appFilter;
	
	@Bean
	public DaoAuthenticationProvider authProvider() {
		
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(usersService);
		
		authProvider.setPasswordEncoder(pwdEncoder);

		return authProvider;
	}
	
	@Bean
	@SneakyThrows
	public AuthenticationManager authManager(AuthenticationConfiguration config) {
		
		return config.getAuthenticationManager();
	}

	@Bean
	@SneakyThrows
	public SecurityFilterChain filterChain(HttpSecurity http) {

	    http.csrf(csrf -> csrf.disable())
	       .authorizeHttpRequests(req -> req
	           .requestMatchers(
	                   "/api/auth/login",
	                   "/api/auth/refresh-token",
	                   "/api/auth/logout",
	                   "/api/admin/register",
	                   "/api/admin/addHr",
	                   "/api/hr/addEmployee"
	           ).permitAll()
	           .anyRequest().authenticated()
	       )
	       .authenticationProvider(authProvider())
	       .addFilterBefore(appFilter, UsernamePasswordAuthenticationFilter.class);

	    return http.build();
	}

}