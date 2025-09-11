package com.example.Crisis_response.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Crisis_response.Entity.User.AuthActivityEntity;
import com.example.Crisis_response.Entity.User.UserAuthEntity;


@Repository
public interface AuthActivityRepository extends JpaRepository<AuthActivityEntity, Integer> {
    Optional<AuthActivityEntity> findTopByUserAuthAndLogoutAtIsNullOrderByLoginAtDesc(UserAuthEntity userAuth);
}
