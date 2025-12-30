package com.neb.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neb.dto.ForgotPasswordRequest;
import com.neb.dto.ResetPasswordRequest;
import com.neb.dto.ResponseMessage;
import com.neb.dto.VerifyOtpRequest;
import com.neb.dto.user.AuthResponse;
import com.neb.dto.user.LoginRequest;
import com.neb.dto.user.LoginResponse;
import com.neb.dto.user.RefreshTokenResponse;
import com.neb.service.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth/")
public class AuthController {

    @Autowired
    private AuthService authService;


    // LOGIN ---------------------------------------------------------------------
    @PostMapping("/login")
    public ResponseEntity<ResponseMessage<LoginResponse>> login(@RequestBody LoginRequest req, HttpServletResponse response) {

        try {
        	
            AuthResponse authResponse = authService.login(req);
            
            Cookie cookie = new Cookie("refreshToken",authResponse.getRefreshToken());
            
            cookie.setHttpOnly(true);
            cookie.setSecure(false); // only if using HTTPS
            cookie.setPath("/");    // set path according to your API routes
            // optionally set SameSite, max-age etc:
            cookie.setMaxAge(7 * 24 * 60 * 60); // e.g. 7 days
            cookie.setAttribute("SameSite", "Lax");
            response.addCookie(cookie);

            
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setAccessToken(authResponse.getAccessToken());
            loginResponse.setRoles(authResponse.getRoles());
            loginResponse.setDashboard(authResponse.getDashboard());

            return ResponseEntity.ok(
                new ResponseMessage<>(200, "SUCCESS", "Login successful", loginResponse)
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ResponseMessage<>(401, "FAILED", e.getMessage()));
        }
    }


    // REFRESH TOKEN --------------------------------------------------------------
    @PostMapping("/refresh-token")
    public ResponseEntity<ResponseMessage<RefreshTokenResponse>> refreshToken(HttpServletRequest request) {
    	
    	String refreshToken = null;
    	System.out.println("refresh token checking with cookies : "+request.getCookies());
    	if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ResponseMessage<>(403, "FAILED", "Refresh token not provided"));
        }
        try {
            AuthResponse resp = authService.refreshAccessToken(refreshToken);
            
            RefreshTokenResponse refreshResponse = new RefreshTokenResponse();
            refreshResponse.setAccessToken(resp.getAccessToken());
            
            System.out.println("refresh-token successfuly created");
            return ResponseEntity.ok(
                new ResponseMessage<>(200, "SUCCESS", "Token refreshed successfully", refreshResponse)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ResponseMessage<>(403, "FAILED", e.getMessage()));
        }
    	
    }


    // LOGOUT ----------------------------------------------------------------------
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public ResponseEntity<ResponseMessage<String>> logout(HttpServletRequest request, HttpServletResponse response) {
        
    	String refreshToken = null;
    	System.out.println(request.getCookies());
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseMessage<>(400, "FAILED", "Refresh token not provided"));
        }
        try {
            String msg = authService.logout(refreshToken);

            // clear cookie
            Cookie deleteCookie = new Cookie("refreshToken", null);
            deleteCookie.setHttpOnly(true);
            deleteCookie.setSecure(false);
            deleteCookie.setPath("/");
            deleteCookie.setMaxAge(0);
            deleteCookie.setAttribute("SameSite", "Lax");
            response.addCookie(deleteCookie);
            
            System.out.println("logout successful");

            return ResponseEntity.ok(new ResponseMessage<>(200, "SUCCESS", msg));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseMessage<>(400, "FAILED", e.getMessage()));
        }
    }
    // ===================== FORGOT PASSWORD =====================
    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseMessage<String>> forgotPassword(
            @RequestBody ForgotPasswordRequest request) {

        authService.sendForgotPasswordOtp(request.getEmail());

        return ResponseEntity.ok(
            new ResponseMessage<>(200, "SUCCESS", "OTP sent to registered email")
        );
    }

    // ====================== VERIFY OTP ======================
    @PostMapping("/verify-forgot-otp")
    public ResponseEntity<ResponseMessage<String>> verifyForgotOtp(
            @RequestBody VerifyOtpRequest request) {

        authService.verifyForgotPasswordOtp(
            request.getEmail(),
            request.getOtp()
        );

        return ResponseEntity.ok(
            new ResponseMessage<>(200, "SUCCESS", "OTP verified successfully")
        );
    }

    // ====================== RESET PASSWORD ======================
    @PostMapping("/reset-password")
    public ResponseEntity<ResponseMessage<String>> resetPassword(
            @RequestBody ResetPasswordRequest request) {

        authService.resetPassword(
            request.getEmail(),
            request.getNewPassword(),
            request.getConfirmPassword()
        );

        return ResponseEntity.ok(
            new ResponseMessage<>(200, "SUCCESS", "Password reset successful")
        );
    }
    
}
