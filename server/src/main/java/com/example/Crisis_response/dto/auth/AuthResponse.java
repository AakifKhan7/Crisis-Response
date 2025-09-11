package com.Aakifkhan.BazarBook.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    public AuthResponse(String accessToken) {
        this.accessToken = accessToken;
    }
    private String accessToken;
    private String tokenType = "Bearer";
}
