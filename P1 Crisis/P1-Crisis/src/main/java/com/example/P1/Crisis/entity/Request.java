package com.example.P1.Crisis.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "request")
@Builder
public class Request {
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
