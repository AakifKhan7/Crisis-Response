package com.example.Crisis_response.services;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.Crisis_response.Entity.User.UserAuthEntity;
import com.example.Crisis_response.Repository.AuthActivityRepository;
import com.example.Crisis_response.Entity.User.AuthActivityEntity;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuthActivityService {

    @Autowired
    private AuthActivityRepository repository;

    private String resolveClientIp() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return "UNKNOWN";
        HttpServletRequest request = attrs.getRequest();
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * Persist a new login (or signup-complete) event.
     */
    @Transactional
    public void recordLogin(UserAuthEntity userAuth) {
        AuthActivityEntity activity = new AuthActivityEntity();
        activity.setUserAuth(userAuth);
        activity.setIpAddress(resolveClientIp());
        activity.setLoginAt(new Timestamp(System.currentTimeMillis()));
        // Audit fields
        activity.setCreatedBy(userAuth.getUser());
        activity.setUpdatedBy(userAuth.getUser());
        repository.save(activity);
    }

    /**
     * Update the latest open AuthActivity row to mark logout time.
     */
    @Transactional
    public void recordLogout(UserAuthEntity userAuth) {
        repository.findTopByUserAuthAndLogoutAtIsNullOrderByLoginAtDesc(userAuth)
                .ifPresent(a -> {
                    a.setLogoutAt(new Timestamp(System.currentTimeMillis()));
                    a.setUpdatedBy(userAuth.getUser());
                    repository.save(a);
                });
    }
}
