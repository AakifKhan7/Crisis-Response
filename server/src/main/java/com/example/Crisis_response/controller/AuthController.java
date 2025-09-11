package com.example.Crisis_response.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Crisis_response.dto.auth.AuthResponse;
import com.example.Crisis_response.dto.auth.LoginRequest;
import com.example.Crisis_response.dto.auth.SignupRequest;
import com.example.Crisis_response.services.AuthService;
import com.example.Crisis_response.services.AuthActivityService;
import org.springframework.security.core.Authentication;

import com.example.Crisis_response.security.CustomUserDetails;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthActivityService authActivityService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.ok(authService.registerUserStart(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.authenticateUser(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(Authentication authentication) {
        CustomUserDetails principal =
                (CustomUserDetails) authentication.getPrincipal();
        authActivityService.recordLogout(principal.getUserAuth());
        return ResponseEntity.ok("Logged out");
    }
}
