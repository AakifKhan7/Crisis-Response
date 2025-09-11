package com.Aakifkhan.BazarBook.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignupInitResponse {
    private boolean otpSent;
    private String maskedPhone;
    private String message;
}


