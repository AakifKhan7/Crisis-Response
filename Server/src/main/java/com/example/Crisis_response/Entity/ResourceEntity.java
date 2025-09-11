package com.example.Crisis_response.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "resources")
@Data
public class ResourceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type; // AMBULANCE, FOOD, SHELTER
    private String location;
    private Boolean available;
}
