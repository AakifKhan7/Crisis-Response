package com.example.P1.Crisis.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestDTO {
    private String description;
    private String type;
    private Integer severity;
    private String location;
}
