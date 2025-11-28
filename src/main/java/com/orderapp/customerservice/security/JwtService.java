package com.orderapp.customerservice.security;

import com.orderapp.customerservice.entity.customerdb.Customer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtService {

    private static final String SECRET_KEY =
            "bXktdmVyeS1sb25nLWFuZC1zZWN1cmUtand0LXNlY3JldC1rZXktMTIzNDU2Nzg5MDEyMzQ1Ng==";

    // 15 phút
    private static final long ACCESS_TOKEN_EXPIRE_SECONDS = 15 * 60;
    // 7 ngày
    private static final long REFRESH_TOKEN_EXPIRE_SECONDS = 7 * 24 * 60 * 60;

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(Customer customer) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("phone", customer.getPhone());
        claims.put("token_type", "ACCESS");
        return buildToken(claims, customer.getId(), ACCESS_TOKEN_EXPIRE_SECONDS);
    }

    public String generateRefreshToken(Customer customer) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("phone", customer.getPhone());
        claims.put("token_type", "REFRESH");
        return buildToken(claims, customer.getId(), REFRESH_TOKEN_EXPIRE_SECONDS);
    }

    private String buildToken(Map<String, Object> claims, String subject, long expiresInSeconds) {
        Instant now = Instant.now();
        Date issuedAt = Date.from(now);
        Date expiration = Date.from(now.plusSeconds(expiresInSeconds));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractSubject(String token) {
        return getAllClaims(token).getSubject();
    }

    public Date extractExpiration(String token) {
        return getAllClaims(token).getExpiration();
    }

    public boolean isAccessToken(String token) {
        String type = getAllClaims(token).get("token_type", String.class);
        return "ACCESS".equals(type);
    }

    public boolean isRefreshToken(String token) {
        String type = getAllClaims(token).get("token_type", String.class);
        return "REFRESH".equals(type);
    }

    public boolean isTokenNonExpired(String token) {
        try {
            Date exp = extractExpiration(token);
            return exp != null && exp.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateAccessToken(String token, Customer customer) {
        try {
            Claims claims = getAllClaims(token);
            String subject = claims.getSubject();
            Date exp = claims.getExpiration();
            String type = claims.get("token_type", String.class);

            return subject != null
                    && subject.equals(customer.getId())
                    && exp != null
                    && exp.after(new Date())
                    && "ACCESS".equals(type);
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
