package com.orderapp.customerservice.controller.auth;

import com.orderapp.customerservice.dto.auth.*;
import com.orderapp.customerservice.dto.customer.CustomerDto;
import com.orderapp.customerservice.entity.customerdb.Customer;
import com.orderapp.customerservice.entity.customerdb.TokenBlacklist;
import com.orderapp.customerservice.repository.customerdb.TokenBlacklistRepository;
import com.orderapp.customerservice.security.JwtService;
import com.orderapp.customerservice.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

@RestController
@RequestMapping("/auth/customer")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            LocalDateTime expiredAt;
            try {
                expiredAt = jwtService.extractExpiration(token)
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
            } catch (Exception e) {
                expiredAt = LocalDateTime.now().plusMinutes(15);
            }

            TokenBlacklist item = TokenBlacklist.builder()
                    .token(token)
                    .createdAt(LocalDateTime.now())
                    .expiredAt(expiredAt)
                    .build();

            tokenBlacklistRepository.save(item);
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/infor")
    public CustomerDto me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Customer customer)) {
            throw new RuntimeException("Không tìm thấy thông tin partner trong context");
        }

        CustomerDto dto = new CustomerDto();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setPhone(customer.getPhone());
        dto.setEmail(customer.getEmail());
        if (customer.getStatus() != null) {
            dto.setStatus(customer.getStatus().name());
        }
        dto.setVerified(customer.getVerified());

        return dto;
    }


    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestBody SendOtpRequest request) {
        // Gọi service và hứng lấy code
        String otpCode = authService.sendOtpLogin(request);
        return ResponseEntity.ok("Mã OTP của bạn là: " + otpCode);
    }

    @PostMapping("/login-otp")
    public ResponseEntity<AuthResponse> loginWithOtp(@RequestBody VerifyOtpLoginRequest request) {
        return ResponseEntity.ok(authService.loginWithOtp(request));
    }
}
