package com.example.Crisis_response.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import java.util.Collections;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Crisis_response.dto.auth.AuthResponse;
import com.example.Crisis_response.dto.auth.LoginRequest;
import com.example.Crisis_response.dto.auth.SignupRequest;
import com.example.Crisis_response.Entity.User.UserAuthEntity;
import com.example.Crisis_response.Entity.User.UserEntity;
import com.example.Crisis_response.Entity.User.UserRollEntity;
import com.example.Crisis_response.Repository.UserAuthRepository;
import com.example.Crisis_response.Repository.UserRepository;
import com.example.Crisis_response.Repository.UserRollRepository;
import com.example.Crisis_response.security.JwtTokenProvider;
import com.example.Crisis_response.security.CustomUserDetails;

import com.example.Crisis_response.Seeder.SuperAdminSeeder;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAuthRepository userAuthRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AuthActivityService authActivityService;

    @Autowired
    private UserRollRepository userRollRepository;

    @Transactional
    public AuthResponse registerUserStart(SignupRequest signUpRequest) {
       
        // Resolve audit user (Super Admin) and default role before persisting
        UserAuthEntity superAdmin = userAuthRepository.findByEmail(SuperAdminSeeder.SUPERADMIN_EMAIL)
                .orElseThrow(() -> new IllegalStateException("Super admin not seeded"));
        UserRollEntity defaultRole = userRollRepository.findByRoll("USER")
                .orElseThrow(() -> new IllegalStateException("USER role not found"));

        UserEntity user = new UserEntity();
        user.setName(signUpRequest.getName());
        user.setPhoneNumber(signUpRequest.getPhoneNumber());
        user.setUserRoll(defaultRole);
        user.setCreatedBy(superAdmin.getUser());
        user.setUpdatedBy(superAdmin.getUser());
        userRepository.save(user);

        UserAuthEntity auth = new UserAuthEntity();
        auth.setEmail(signUpRequest.getEmail());
        auth.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        auth.setUser(user);
        auth.setCreatedBy(superAdmin.getUser());
        auth.setUpdatedBy(superAdmin.getUser());
        userAuthRepository.save(auth);

        return authenticateUser(new LoginRequest(signUpRequest.getEmail(), signUpRequest.getPassword()));
    }

    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        String token = tokenProvider.generateToken(authentication);
        // Log activity
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        authActivityService.recordLogin(principal.getUserAuth());
        return new AuthResponse(token);
    }

    @Transactional
    public AuthResponse completeSignupAndLogin(String phone) {
        UserEntity user = userRepository.findByPhoneNumber(phone).orElseThrow(() -> new RuntimeException("User not found"));
        if (!user.isPhoneVerified()) {
            user.setPhoneVerified(true);
            userRepository.save(user);
        }
        UserAuthEntity auth = userAuthRepository.findByUserId(user.getId()).orElseThrow(() -> new RuntimeException("Auth not found"));

        CustomUserDetails principal = new CustomUserDetails(auth);
        Authentication syntheticAuth = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities() == null ? Collections.emptyList() : principal.getAuthorities());
        String token = tokenProvider.generateToken(syntheticAuth);
        // Log activity
        authActivityService.recordLogin(auth);
        return new AuthResponse(token);
    }
}
