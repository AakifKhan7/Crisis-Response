package com.example.Crisis_response.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.Crisis_response.Entity.User.UserRollEntity;

@Repository
public interface UserRollRepository extends JpaRepository<UserRollEntity, Long> {
    @Query("SELECT u FROM UserRollEntity u WHERE u.roll = :roll")
    Optional<UserRollEntity> findByRoll(String roll);
}


