package com.orderapp.customerservice.dto.customer;

import lombok.Data;

@Data
public class ChangePhoneRequest {
    private String newPhone;
    private String otpCode;
}