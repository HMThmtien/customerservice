package com.orderapp.customerservice.service.auth.impl;

import com.orderapp.customerservice.dto.auth.*;
import com.orderapp.customerservice.entity.customerdb.OtpCode;
import com.orderapp.customerservice.entity.customerdb.Customer;
import com.orderapp.customerservice.entity.customerdb.TokenBlacklist;
import com.orderapp.customerservice.repository.customerdb.OtpCodeRepository;
import com.orderapp.customerservice.repository.customerdb.CustomerRepository;
import com.orderapp.customerservice.repository.customerdb.TokenBlacklistRepository;
import com.orderapp.customerservice.security.JwtService;
import com.orderapp.customerservice.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final CustomerRepository customerRepository;
    private final JwtService jwtService;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final OtpCodeRepository otpCodeRepository;

    @Override
    public AuthResponse login(LoginRequest request) {
        Customer customer = customerRepository
                .findByPhoneAndPasswordAndDeletedAtIsNull(request.getPhone(), request.getPassword())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sai số điện thoại hoặc mật khẩu"));

        customer.setLastLoginDate(LocalDateTime.now());
        customer.setLogin(true);

        String accessToken = jwtService.generateAccessToken(customer);
        String refreshToken = jwtService.generateRefreshToken(customer);

        // nếu anh muốn lưu refreshToken gần nhất vào DB
        customer.setRefreshToken(refreshToken);
        customerRepository.save(customer);

        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String oldRefreshToken = request.getRefreshToken();

        // 1. Kiểm tra token đã blacklist chưa
        if (!jwtService.isRefreshToken(oldRefreshToken) ||
                !jwtService.isTokenNonExpired(oldRefreshToken) ||
                tokenBlacklistRepository.existsByToken(oldRefreshToken)) {
            throw new RuntimeException("Refresh token không hợp lệ");
        }

        String CustomerId = jwtService.extractSubject(oldRefreshToken);

        Customer customer = customerRepository
                .findByIdAndDeletedAtIsNull(CustomerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy partner"));

        // (Optional) kiểm tra khớp với refreshToken lưu trong DB
        if (customer.getRefreshToken() != null &&
                !oldRefreshToken.equals(customer.getRefreshToken())) {
            throw new RuntimeException("Refresh token không khớp");
        }

        // 2. Blacklist refresh token cũ
        TokenBlacklist item = TokenBlacklist.builder()
                .token(oldRefreshToken)
                .createdAt(LocalDateTime.now())
                .expiredAt(jwtService.extractExpiration(oldRefreshToken)
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime())
                .build();
        tokenBlacklistRepository.save(item);

        // 3. Tạo cặp token mới
        String newAccessToken = jwtService.generateAccessToken(customer);
        String newRefreshToken = jwtService.generateRefreshToken(customer);

        customer.setRefreshToken(newRefreshToken);
        customerRepository.save(customer);

        return new AuthResponse(newAccessToken, newRefreshToken);
    }


    @Override
    public String sendOtpLogin(SendOtpRequest request) {
        String phone = request.getPhone();

        // 1. Kiểm tra tồn tại
        boolean exists = customerRepository.findByPhoneAndDeletedAtIsNull(phone).isPresent();
        if (!exists) {
            throw new RuntimeException("Số điện thoại chưa được đăng ký đối tác");
        }

        // 2. Sinh mã
        String code = String.format("%06d", new Random().nextInt(999999));

        // 3. Lưu DB
        OtpCode otp = OtpCode.builder()
                .phone(phone)
                .code(code)
                .type(OtpCode.OtpType.LOGIN)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .isUsed(false)
                .attempts(0)
                .createdAt(LocalDateTime.now())
                .build();

        otpCodeRepository.save(otp);

        // Log ra console (giữ nguyên để debug)
        System.out.println(">>> GỬI OTP LOGIN ĐẾN " + phone + ": " + code);

        // 4. TRẢ VỀ MÃ CODE (Thay đổi ở đây)
        return code;
    }

    @Override
    public AuthResponse loginWithOtp(VerifyOtpLoginRequest request) {
        String phone = request.getPhone();
        String inputCode = request.getOtpCode();

        // 1. Tìm bản ghi OTP hợp lệ gần nhất
        OtpCode otpEntity = otpCodeRepository.findFirstByPhoneAndTypeAndIsUsedFalseOrderByCreatedAtDesc(
                phone, OtpCode.OtpType.LOGIN
        ).orElseThrow(() -> new RuntimeException("Yêu cầu gửi mã OTP trước"));

        // 2. Kiểm tra hạn sử dụng
        if (otpEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Mã OTP đã hết hạn");
        }

        // 3. Kiểm tra mã khớp không
        if (!otpEntity.getCode().equals(inputCode)) {
            // Tăng số lần sai (optional logic)
            otpEntity.setAttempts(otpEntity.getAttempts() + 1);
            otpCodeRepository.save(otpEntity);
            throw new RuntimeException("Mã OTP không chính xác");
        }

        // 4. Đánh dấu đã sử dụng
        otpEntity.setIsUsed(true);
        otpCodeRepository.save(otpEntity);

        // 5. Lấy thông tin Partner (Đã check tồn tại ở bước gửi OTP, nhưng check lại cho chắc)
        Customer customer = customerRepository.findByPhoneAndDeletedAtIsNull(phone)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy partner"));

        // 6. Cập nhật trạng thái đăng nhập
        customer.setLastLoginDate(LocalDateTime.now());
        customer.setLogin(true);
        // partner.setIsVerified(true); // Có thể auto verify sdt nếu đăng nhập thành công qua OTP

        // 7. Sinh Token (Logic giống hệt login password)
        String accessToken = jwtService.generateAccessToken(customer);
        String refreshToken = jwtService.generateRefreshToken(customer);

        customer.setRefreshToken(refreshToken);
        customerRepository.save(customer);

        return new AuthResponse(accessToken, refreshToken);
    }

}
