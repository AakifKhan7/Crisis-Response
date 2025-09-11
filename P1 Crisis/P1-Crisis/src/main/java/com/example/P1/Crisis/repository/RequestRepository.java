package com.example.P1.Crisis.repository;

import com.example.P1.Crisis.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<Request, Long> {
}
