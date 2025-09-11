package com.example.Crisis_response.dto;

import lombok.Data;

@Data
public class RequestDTO {
    private String description;
    private String type;
    private Integer severity;
    private String location;
}
