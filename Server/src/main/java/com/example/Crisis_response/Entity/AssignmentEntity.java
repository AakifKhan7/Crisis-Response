package com.example.Crisis_response.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "assignments")
@Data
public class AssignmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private RequestEntity request;

    @ManyToOne
    @JoinColumn(name = "resource_id")
    private ResourceEntity resource;

    private LocalDateTime assignedAt;
    private String status; // ASSIGNED, IN_PROGRESS, COMPLETED
}
