package com.orderapp.customerservice.repository.shareddb;

import com.orderapp.customerservice.entity.shareddb.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, String> {

    // 1. Lấy toàn bộ tin nhắn của một phòng (Booking ID), sắp xếp cũ nhất -> mới nhất
    // Dùng để load lịch sử chat khi người dùng mới vào
    List<Message> findByRoomIdOrderByCreatedAtAsc(String roomId);

    // 2. Lấy tin nhắn có phân trang (Lazy load/Infinite scroll)
    // Thường sẽ load mới nhất trước (Desc)
    Page<Message> findByRoomId(String roomId, Pageable pageable);

    // 3. Đếm số tin nhắn chưa đọc của user trong 1 phòng
    Long countByRoomIdAndReceiverIdAndIsReadFalse(String roomId, String receiverId);
}