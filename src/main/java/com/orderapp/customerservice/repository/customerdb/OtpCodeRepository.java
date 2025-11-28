package com.orderapp.customerservice.repository.customerdb;

import com.orderapp.customerservice.entity.customerdb.OtpCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpCodeRepository extends JpaRepository<OtpCode, String> {

    // Tìm mã OTP mới nhất còn hiệu lực của SĐT này và loại LOGIN
    Optional<OtpCode> findFirstByPhoneAndTypeAndIsUsedFalseOrderByCreatedAtDesc(
            String phone,
            OtpCode.OtpType type
    );
}