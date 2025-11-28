package com.orderapp.customerservice.dto.auth;

import lombok.Data;

@Data
public class SendOtpRequest {
    private String phone;
}