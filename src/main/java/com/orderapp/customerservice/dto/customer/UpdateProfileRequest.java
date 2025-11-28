package com.orderapp.customerservice.dto.customer;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateProfileRequest {
    private String name;
    private String email;
    private String avatar;
    private LocalDate dateOfBirth;
    private String operatingHours; // JSON format
}