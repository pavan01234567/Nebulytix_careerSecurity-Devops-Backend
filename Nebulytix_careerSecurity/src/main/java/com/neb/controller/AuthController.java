package com.neb.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neb.entity.RefreshToken;
import com.neb.entity.Users;
import com.neb.service.JwtService;
import com.neb.service.RefreshTokenService;
import com.neb.service.UsersService;

@RestController
@RequestMapping("/api/auth/")
public class AuthController {

	@Autowired
	private UsersService usersService;
	
	@Autowired
	private RefreshTokenService refreshTokenService;
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private AuthenticationManager authManager;
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Users users) {

	    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(users.getEmail(), users.getPassword());

	    try {
	        Authentication authenticate = authManager.authenticate(token);

	        if (authenticate.isAuthenticated()) {
	
	            Users userEntity = usersService.findByEmail(users.getEmail());

	            String jwtToken = jwtService.generateToken(users.getEmail());
	            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userEntity);

	            Map<String, String> tokens = new HashMap<>();
	            tokens.put("accessToken", jwtToken);
	            tokens.put("refreshToken", refreshToken.getToken());

	            return ResponseEntity.ok(tokens);
	        }

	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Credentials");
	    }

	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Credentials");
	}
	
	@PostMapping("/refresh-token")
	public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
	    String requestRefreshToken = request.get("refreshToken");

	    RefreshToken refreshToken = refreshTokenService.findByToken(requestRefreshToken);

	    if (refreshToken == null || refreshTokenService.isTokenExpired(refreshToken)) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Refresh token expired or invalid!");
	    }

	    String newAccessToken = jwtService.generateToken(refreshToken.getUser().getEmail());
	    Map<String, String> tokenResponse = new HashMap<>();
	    tokenResponse.put("accessToken", newAccessToken);
	    tokenResponse.put("refreshToken", requestRefreshToken); // keep same refresh token

	    return ResponseEntity.ok(tokenResponse);
	}
	
	@PostMapping("/logout")
	public ResponseEntity<?> logout(@RequestBody Map<String, String> request) {
	    String refreshToken = request.get("refreshToken");
	    RefreshToken tokenEntity = refreshTokenService.findByToken(refreshToken);

	    if (tokenEntity != null) {
	        refreshTokenService.deleteByUser(tokenEntity.getUser());
	    }

	    return ResponseEntity.ok("Logout successful");
	}
	
	@GetMapping("/welcome")
	public String welcome() {
		return "welcome to Nebulytix technologies";
	}
}
