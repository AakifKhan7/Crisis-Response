package com.example.Crisis_response.dto;

import lombok.Data;

@Data
public class ResourceDTO {
    private String name;
    private String type;
    private String location;
    private Boolean available;
}
