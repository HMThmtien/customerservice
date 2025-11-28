package com.orderapp.customerservice.dto.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String phone;
    private String password;
}
