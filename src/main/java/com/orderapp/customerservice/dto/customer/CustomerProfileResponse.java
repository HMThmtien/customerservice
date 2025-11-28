package com.orderapp.customerservice.dto.customer;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CustomerProfileResponse {
    private String id;
    private String name;
    private String phone;
    private String email;
    private String avatar;
    private LocalDate dateOfBirth;
    private String status;
    private Boolean isVerified;
    private String type;
    private String referralCode;
    private BigDecimal walletBalance;
    private String operatingHours;
}