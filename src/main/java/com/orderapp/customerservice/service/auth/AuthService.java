package com.orderapp.customerservice.service.auth;

import com.orderapp.customerservice.dto.auth.*;

public interface AuthService {
    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);

    String sendOtpLogin(SendOtpRequest request);

    AuthResponse loginWithOtp(VerifyOtpLoginRequest request);
}
