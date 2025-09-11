package com.example.Crisis_response.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Data
public class RequestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private String type; // MEDICAL, FOOD, SHELTER, RESCUE
    private Integer severity; // 1=low, 2=medium, 3=high
    private String location;
    private String status; // PENDING, ASSIGNED, RESOLVED
    private LocalDateTime createdAt;
}
