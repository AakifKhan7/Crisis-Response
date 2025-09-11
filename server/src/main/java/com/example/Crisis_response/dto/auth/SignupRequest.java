package com.example.Crisis_response.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SignupRequest {
    @NotBlank
    private String name;

    @NotBlank
    @Pattern(regexp = "^(\\+?[1-9][0-9]{7,14})$", message = "Phone must be digits with optional leading +, 8-15 digits total")
    private String phoneNumber;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
