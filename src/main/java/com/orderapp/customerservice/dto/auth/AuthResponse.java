package com.orderapp.customerservice.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response chuẩn cho mọi auth API:
 * - accessToken: dùng để gọi API bình thường
 * - refreshToken: dùng để gọi /auth/refresh
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
}
