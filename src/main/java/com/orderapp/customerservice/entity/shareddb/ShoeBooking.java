package com.orderapp.customerservice.entity.shareddb;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "shoe_bookings")
@Getter
@Setter
@NoArgsConstructor
public class ShoeBooking {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "order_id", nullable = false, unique = true)
    private String orderId;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "partner_id")
    private String partnerId;

    @Column(name = "status")
    private String status;

    @Column(name = "original_price")
    private BigDecimal originalPrice;

    @Column(name = "final_price")
    private BigDecimal finalPrice;

    @Column(name = "booking_date")
    private OffsetDateTime bookingDate;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;
}
