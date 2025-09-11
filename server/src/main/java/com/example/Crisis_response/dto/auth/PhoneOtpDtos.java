package com.Aakifkhan.BazarBook.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

public class PhoneOtpDtos {

    @Data
    public static class SendOtpRequest {
        @NotBlank
        @Pattern(regexp = "^[+][1-9][0-9]{7,14}$", message = "Phone must be in E.164 format, e.g., +15551234567")
        private String phoneNumber;
    }

    @Data
    public static class VerifyOtpRequest {
        @NotBlank
        @Pattern(regexp = "^[+][1-9][0-9]{7,14}$", message = "Phone must be in E.164 format")
        private String phoneNumber;

        @NotBlank
        @Pattern(regexp = "^[0-9]{4,8}$", message = "OTP must be numeric")
        private String otp;
    }
}


