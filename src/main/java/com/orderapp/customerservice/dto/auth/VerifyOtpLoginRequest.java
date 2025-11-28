package com.orderapp.customerservice.dto.auth;

import lombok.Data;

@Data
public class VerifyOtpLoginRequest {
    private String phone;
    private String otpCode;
}