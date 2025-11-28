package com.orderapp.customerservice.repository.customerdb;

import com.orderapp.customerservice.entity.customerdb.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, String> {

    Optional<Customer> findByPhoneAndPasswordAndDeletedAtIsNull(String phone, String password);

    Optional<Customer> findByIdAndDeletedAtIsNull(String id);

    Optional<Customer> findByPhoneAndDeletedAtIsNull(String phone);
}
