package com.example.Crisis_response.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Crisis_response.Entity.User.UserAuthEntity;

@Repository
public interface UserAuthRepository extends JpaRepository<UserAuthEntity, Long> {
    Optional<UserAuthEntity> findByEmail(String email);
    Optional<UserAuthEntity> findByUserId(Long userId);
}
