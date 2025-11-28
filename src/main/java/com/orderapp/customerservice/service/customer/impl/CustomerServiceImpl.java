package com.orderapp.customerservice.service.customer.impl;

import com.orderapp.customerservice.dto.customer.ChangePasswordRequest;
import com.orderapp.customerservice.dto.customer.ChangePhoneRequest;
import com.orderapp.customerservice.dto.customer.CustomerProfileResponse;
import com.orderapp.customerservice.dto.customer.UpdateProfileRequest;
import com.orderapp.customerservice.entity.customerdb.Customer;
import com.orderapp.customerservice.entity.customerdb.OtpCode;
//import com.orderapp.customerservice.entity.customerdb.Wallet;
import com.orderapp.customerservice.repository.customerdb.CustomerRepository;
import com.orderapp.customerservice.repository.customerdb.OtpCodeRepository;
//import com.orderapp.customerservice.repository.customerdb.WalletRepository;
import com.orderapp.customerservice.service.customer.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
   // private final WalletRepository walletRepository;
    private final OtpCodeRepository otpCodeRepository;

    // Helper: Lấy Partner hiện tại từ SecurityContext
    private Customer getCurrentPartner() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Customer)) {
            throw new RuntimeException("Không tìm thấy thông tin người dùng");
        }
        Customer principal = (Customer) authentication.getPrincipal();
        // Fetch lại từ DB để đảm bảo data mới nhất
        return customerRepository.findById(principal.getId())
                .orElseThrow(() -> new RuntimeException("Partner không tồn tại"));
    }

    @Override
    public CustomerProfileResponse getProfile() {
        Customer customer = getCurrentPartner();

        // Lấy ví (nếu chưa có thì tạo ví ảo số dư 0 để trả về)
//        Wallet wallet = walletRepository.findByPartnerId(partner.getId())
//                .orElse(Wallet.builder().balance(BigDecimal.ZERO).build());

        CustomerProfileResponse response = new CustomerProfileResponse();
        response.setId(customer.getId());
        response.setName(customer.getName());
        response.setPhone(customer.getPhone());
        response.setEmail(customer.getEmail());
        response.setAvatar(customer.getAvatar());
        response.setDateOfBirth(customer.getDateOfBirth());
        response.setStatus(customer.getStatus() != null ? customer.getStatus().name() : null);
        response.setIsVerified(customer.getVerified());
        response.setReferralCode(customer.getReferralCode());

        // Set Wallet Balance
    //    response.setWalletBalance(wallet.getBalance());

        return response;
    }

    @Override
    public CustomerProfileResponse updateProfile(UpdateProfileRequest request) {
        Customer customer = getCurrentPartner();

        if (request.getName() != null) customer.setName(request.getName());
        if (request.getEmail() != null) customer.setEmail(request.getEmail());
        if (request.getAvatar() != null) customer.setAvatar(request.getAvatar());
        if (request.getDateOfBirth() != null) customer.setDateOfBirth(request.getDateOfBirth());

        customerRepository.save(customer);
        return getProfile(); // Trả về profile mới
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        Customer customer = getCurrentPartner();

        // Kiểm tra mật khẩu cũ (Lưu ý: Nếu bạn dùng BCrypt thì phải dùng passwordEncoder.matches)
        // Hiện tại code cũ bạn đang dùng plain text nên so sánh equals
        if (!customer.getPassword().equals(request.getOldPassword())) {
            throw new RuntimeException("Mật khẩu cũ không chính xác");
        }

        customer.setPassword(request.getNewPassword());
        customerRepository.save(customer);
    }

    @Override
    public void changePhone(ChangePhoneRequest request) {
        Customer customer = getCurrentPartner();
        String newPhone = request.getNewPhone();

        // 1. Check số điện thoại mới đã tồn tại chưa
        if (customerRepository.findByPhoneAndDeletedAtIsNull(newPhone).isPresent()) {
            throw new RuntimeException("Số điện thoại này đã được sử dụng");
        }

        // 2. Verify OTP (Logic giống login OTP nhưng type là CHANGE_PHONE)
        // Lưu ý: Client phải gọi API gửi OTP với type CHANGE_PHONE trước khi gọi API này
        // Ở đây mình tạm dùng type LOGIN nếu bạn chưa làm type CHANGE_PHONE,
        // nhưng đúng chuẩn là phải check type = CHANGE_PHONE

        OtpCode otpEntity = otpCodeRepository.findFirstByPhoneAndTypeAndIsUsedFalseOrderByCreatedAtDesc(
                newPhone, // Check OTP của số điện thoại MỚI
                OtpCode.OtpType.LOGIN // Tạm dùng LOGIN, bạn nên thêm enum CHANGE_PHONE vào OtpCode
        ).orElseThrow(() -> new RuntimeException("OTP không hợp lệ hoặc hết hạn"));

        if (!otpEntity.getCode().equals(request.getOtpCode())) {
            otpEntity.setAttempts(otpEntity.getAttempts() + 1);
            otpCodeRepository.save(otpEntity);
            throw new RuntimeException("Mã OTP không chính xác");
        }

        if (otpEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Mã OTP đã hết hạn");
        }

        // 3. Đánh dấu OTP đã dùng
        otpEntity.setIsUsed(true);
        otpCodeRepository.save(otpEntity);

        // 4. Update Phone
        customer.setPhone(newPhone);
        customerRepository.save(customer);
    }

    @Override
    public String getPresignedUrl(String fileName) {
        // TODO: Tích hợp AWS S3 hoặc MinIO SDK vào đây
        // Đây là code giả lập trả về URL dummy
        return "https://s3.amazonaws.com/your-bucket/" + fileName + "?signed=true&token=xyz";
    }
}