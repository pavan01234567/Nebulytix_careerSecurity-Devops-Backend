package com.neb.service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.neb.constants.Role;
import com.neb.dto.user.AuthResponse;
import com.neb.dto.user.LoginRequest;
import com.neb.entity.PasswordResetOtp;
import com.neb.entity.RefreshToken;
import com.neb.entity.Users;
import com.neb.exception.EmailNotFoundException;
import com.neb.exception.InvalidOtpException;
import com.neb.exception.OtpAttemptsExceededException;
import com.neb.exception.PasswordMismatchException;
import com.neb.repo.PasswordResetOtpRepository;
import com.neb.repo.UsersRepository;
import com.neb.util.AuthUtils;

import jakarta.transaction.Transactional;

@Service
public class AuthService {


	@Autowired
    private AuthenticationManager authManager;

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private PasswordResetOtpRepository otpRepository;
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private JwtService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    private RefreshTokenService refreshTokenService;
    
    @Autowired
    private UsersRepository userRepository;


    // LOGIN --------------------------------------------------------------------
    public AuthResponse login(LoginRequest req) {

        // 1. Validate credentials using AuthenticationManager
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );

        if (!auth.isAuthenticated()) {
            throw new RuntimeException("Invalid credentials");
        }

        // 2. Fetch user from DB
        Users user = usersRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3. Convert roles to string set
        Set<String> roles = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        // 4. Create access token
        String accessToken = jwtService.generateToken(user.getEmail());

        // 5. Create refresh token object
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        // 6. Prepare response
        AuthResponse resp = new AuthResponse();
        resp.setAccessToken(accessToken);
        resp.setRefreshToken(refreshToken.getToken());
        resp.setRoles(roles);
        resp.setDashboard(getDashboardForUser(user));

        return resp;
    }


    // REFRESH ACCESS TOKEN ------------------------------------------------------
    public AuthResponse refreshAccessToken(String requestRefreshToken) {

        RefreshToken refreshToken = refreshTokenService.findByToken(requestRefreshToken);

        if (refreshToken == null) {
            throw new RuntimeException("Invalid refresh token!");
        }

        if (refreshTokenService.isTokenExpired(refreshToken)) {
            throw new RuntimeException("Refresh token expired! Please login again.");
        }

        // Create new access token
        String newAccessToken = jwtService.generateToken(refreshToken.getUser().getEmail());

        AuthResponse resp = new AuthResponse();
        resp.setAccessToken(newAccessToken);
        resp.setRefreshToken(requestRefreshToken);

        // roles & dashboard also needed on frontend when refreshing
        Users user = refreshToken.getUser();
        Set<String> roles = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        resp.setRoles(roles);
        resp.setDashboard(getDashboardForUser(user));

        return resp;
    }


    // LOGOUT ---------------------------------------------------------------------

    @Transactional
    public String logout(String refreshTokenStr) {
    	
        if (refreshTokenStr == null || refreshTokenStr.isBlank()) {
            throw new IllegalArgumentException("refreshToken is required");
        }

        RefreshToken tokenEntity = refreshTokenService.findByToken(refreshTokenStr);
        if (tokenEntity == null) {
            throw new RuntimeException("Invalid refresh token");
        }

        // Ensure the current authenticated user is the owner of the refresh token
        String currentEmail = AuthUtils.getCurrentUserEmail();
        if (currentEmail == null) {
            throw new RuntimeException("User not authenticated");
        }

        Users tokenOwner = tokenEntity.getUser();
        if (!currentEmail.equals(tokenOwner.getEmail())) {
            // If you want to be strict: throw an error
            throw new RuntimeException("You are not authorized to logout this token");
        }

        // Delete refresh tokens for this user (logout all sessions for this user).
        refreshTokenService.deleteByUser(tokenOwner);

        return "Logout successful";
    }


    // DASHBOARD DECIDER ----------------------------------------------------------
    private String getDashboardForUser(Users user) {

        if (user.getRoles().contains(Role.ROLE_ADMIN)) return "ADMIN_DASHBOARD";
        if (user.getRoles().contains(Role.ROLE_MANAGER)) return "MANAGER_DASHBOARD";
        if (user.getRoles().contains(Role.ROLE_HR)) return "HR_DASHBOARD";
        if (user.getRoles().contains(Role.ROLE_EMPLOYEE)) return "EMPLOYEE_DASHBOARD";
        if (user.getRoles().contains(Role.ROLE_CLIENT)) return "CLIENT_DASHBOARD";

        return "DEFAULT_DASHBOARD";
    }
 // ================= FORGOT PASSWORD =================

    public void sendForgotPasswordOtp(String email) {

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new EmailNotFoundException("Email is not registered")
                );

        // Generate OTP
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);

        PasswordResetOtp resetOtp = new PasswordResetOtp();
        resetOtp.setEmail(email);
        resetOtp.setOtp(otp);
        resetOtp.setUsed(false);
        resetOtp.setAttempts(0); // 🔥 NEW FIELD
        resetOtp.setExpiryTime(LocalDateTime.now().plusMinutes(10));

        otpRepository.save(resetOtp);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset OTP");
        message.setText(
                "Your OTP is: " + otp +
                "\nValid for 10 minutes. Do not share this OTP."
        );

        mailSender.send(message);
    }

    public void verifyForgotPasswordOtp(String email, String otp) {

        PasswordResetOtp resetOtp = otpRepository
                .findTopByEmailAndUsedFalseOrderByExpiryTimeDesc(email)
                .orElseThrow(() ->
                        new InvalidOtpException("OTP not found or already used")
                );

        if (resetOtp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new InvalidOtpException("OTP expired");
        }

        if (resetOtp.getAttempts() >= 3) {
            throw new OtpAttemptsExceededException(
                    "You tried more than 3 times. Please try again later."
            );
        }

        if (!resetOtp.getOtp().equals(otp)) {
            resetOtp.setAttempts(resetOtp.getAttempts() + 1);
            otpRepository.save(resetOtp);
            throw new InvalidOtpException("Invalid OTP");
        }

        resetOtp.setUsed(true);
        otpRepository.save(resetOtp);
    }

    public void resetPassword(String email, String newPassword, String confirmPassword) {

        if (!newPassword.equals(confirmPassword)) {
            throw new PasswordMismatchException("Passwords do not match");
        }

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new EmailNotFoundException("Email is not registered")
                );

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
