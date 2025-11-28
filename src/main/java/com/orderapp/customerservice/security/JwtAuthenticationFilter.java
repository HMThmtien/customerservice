package com.orderapp.customerservice.security;

import com.orderapp.customerservice.entity.customerdb.Customer;
import com.orderapp.customerservice.repository.customerdb.CustomerRepository;
import com.orderapp.customerservice.repository.customerdb.TokenBlacklistRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomerRepository customerRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {


        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        // Nếu token đã bị blacklist thì coi như chưa đăng nhập
        if (tokenBlacklistRepository.existsByToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        String subject;
        try {
            subject = jwtService.extractSubject(token);
        } catch (Exception ex) {
            filterChain.doFilter(request, response);
            return;
        }

        if (subject != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Optional<Customer> optionalPartner =
                    customerRepository.findByIdAndDeletedAtIsNull(subject);

            if (optionalPartner.isPresent() && jwtService.isAccessToken(token)
                    && jwtService.validateAccessToken(token, optionalPartner.get())) {

                Customer customer = optionalPartner.get();

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                customer,
                                null,
                                List.of() // nếu có Role thì map vào đây
                        );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
