package com.neb.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.neb.filter.AppFilter;
import com.neb.service.UsersService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
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
	public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

//	@Bean
//	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//
//	    http
//	        .csrf(csrf -> csrf.disable())
//	        .cors(cors -> {}) // enable cors
//	        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//	        .authenticationProvider(authProvider())
//	        .authorizeHttpRequests(req -> req
//	        		
//	                // allow only specific auth endpoints (login/refresh/register) publicly
//	        		  .requestMatchers("/h2-console/**").permitAll()
//	        		.requestMatchers(
//	                        "/api/auth/forgot-password",
//	                        "/api/auth/verify-forgot-otp",
//	                        "/api/auth/reset-password",
//	                        "/api/auth/login",
//	                        "/api/auth/register" ).permitAll()
//	                .requestMatchers("/api/auth/login","/api/admin/create-admin").permitAll()
//	                .requestMatchers("/api/auth/refresh-token").permitAll()
//	                .requestMatchers("/api/auth/register").permitAll()
//	                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
//	                .requestMatchers("/api/admin/**").hasRole("ADMIN")
//                    .requestMatchers("/api/hr/**").hasAnyRole("HR", "ADMIN")//"ROLE_ADMIN","ROLE_EMPLOYEE","ROLE_HR"
//                    .requestMatchers("/api/employee/**").hasAnyRole("EMPLOYEE")
//                    .anyRequest().authenticated()
//	        )
//	        .addFilterBefore(appFilter, UsernamePasswordAuthenticationFilter.class);
//
//	    return http.build();
//	}
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

	    http
	        .csrf(csrf -> csrf.disable())
	        .cors(cors -> {}) // enable cors
	        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	        .authenticationProvider(authProvider())
	        .authorizeHttpRequests(req -> req
	            .requestMatchers("/h2-console/**").permitAll()
	            .requestMatchers(
	                    "/api/auth/forgot-password",
	                    "/api/auth/verify-forgot-otp",
	                    "/api/auth/reset-password",
	                    "/api/auth/login",
	                    "/api/auth/register").permitAll()
	            .requestMatchers("/api/auth/login","/api/admin/create-admin").permitAll()
	            .requestMatchers("/api/auth/refresh-token").permitAll()
	            .requestMatchers("/api/auth/register").permitAll()
	            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
	            .requestMatchers("/api/admin/**").hasRole("ADMIN")
	            .requestMatchers("/api/hr/**").hasAnyRole("HR", "ADMIN")
	            .requestMatchers("/api/employee/**").hasAnyRole("EMPLOYEE")
	            .anyRequest().authenticated()
	        )
	        .headers(headers -> headers
	            .frameOptions(frameOptions -> frameOptions.sameOrigin()) // allow iframe from same origin
	        )
	        .addFilterBefore(appFilter, UsernamePasswordAuthenticationFilter.class);

	    return http.build();
	}
}

