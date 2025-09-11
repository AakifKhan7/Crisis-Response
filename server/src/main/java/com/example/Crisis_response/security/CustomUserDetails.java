package com.example.Crisis_response.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.core.userdetails.UserDetails;

import com.example.Crisis_response.Entity.User.UserAuthEntity;

public class CustomUserDetails implements UserDetails {

    private final UserAuthEntity userAuth;

    public CustomUserDetails(UserAuthEntity userAuth) {
        this.userAuth = userAuth;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // TODO: map real roles when implemented
        // No role checks for now
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return userAuth.getPassword();
    }

    @Override
    public String getUsername() {
        return userAuth.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !userAuth.isDeleted() && userAuth.getUser() != null && userAuth.getUser().isPhoneVerified();
    }

    public UserAuthEntity getUserAuth() {
        return this.userAuth;
    }

    public Long getUserId() {
        return userAuth.getUser().getId();
    }
}
