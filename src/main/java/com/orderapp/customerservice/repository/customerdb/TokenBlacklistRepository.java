package com.orderapp.customerservice.repository.customerdb;

import com.orderapp.customerservice.entity.customerdb.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, Long> {

    boolean existsByToken(String token);
}
