package com.orderapp.customerservice.service.customer;

import com.orderapp.customerservice.dto.customer.*;

public interface CustomerService {
    CustomerProfileResponse getProfile();

    CustomerProfileResponse updateProfile(UpdateProfileRequest request);

    void changePassword(ChangePasswordRequest request);

    void changePhone(ChangePhoneRequest request);

    String getPresignedUrl(String fileName);

}