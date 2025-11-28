package com.orderapp.customerservice.config;

import com.orderapp.customerservice.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // 1. Cho phép các API Auth
                        .requestMatchers(
                                "/auth/customer/**",        // Bao gồm login, refresh, otp...
                                "/v1/auth/customer/**"      // Dự phòng nếu bạn đổi prefix
                        ).permitAll()
                        .requestMatchers("/ws-chat/**").permitAll()

                        // 2. [QUAN TRỌNG] Cho phép Spring trả về lỗi
                        // Nếu không có dòng này, khi gặp lỗi 404/405, Spring forward về /error
                        // và bị Security chặn lại -> thành 403.
                        .requestMatchers("/error").permitAll()

                        // 3. Các API còn lại bắt buộc đăng nhập
                        .anyRequest().authenticated()
                )
                // 4. [NÊN LÀM] Xử lý lỗi khi chưa đăng nhập (Thiếu token hoặc token sai)
                // Thay vì trả về 403 Forbidden (Mặc định), ta trả về 401 Unauthorized
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
                        })
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}