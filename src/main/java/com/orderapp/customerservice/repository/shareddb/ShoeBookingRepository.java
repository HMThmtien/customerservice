package com.orderapp.customerservice.repository.shareddb;

import com.orderapp.customerservice.entity.shareddb.ShoeBooking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShoeBookingRepository extends JpaRepository<ShoeBooking, String> {

    List<ShoeBooking> findByPartnerIdOrderByCreatedAtDesc(String partnerId);
}
