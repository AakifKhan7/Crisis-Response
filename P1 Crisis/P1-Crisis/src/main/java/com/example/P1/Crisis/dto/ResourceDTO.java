package com.example.P1.Crisis.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceDTO {
    private String name;
    private String type;
    private String location;
    private Boolean available;
}
