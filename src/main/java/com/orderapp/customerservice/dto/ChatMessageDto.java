package com.orderapp.customerservice.dto;

import com.orderapp.customerservice.entity.shareddb.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDto {

    // --- Thông tin bắt buộc khi gửi tin (Request) ---
    private String roomId;        // ID đơn hàng (shoe_booking_id)
    private String senderId;      // ID người gửi
    private String receiverId;    // ID người nhận (Partner hoặc Customer)
    private String content;       // Nội dung tin nhắn

    // --- Thông tin tùy chọn (Request/Response) ---
    // Mặc định là TEXT. Dùng enum của Entity để đồng bộ
    @Builder.Default
    private Message.MessageType messageType = Message.MessageType.TEXT;

    private String attachmentUrl; // URL ảnh/file (nếu messageType là IMAGE/FILE)

    // --- Thông tin chỉ dùng khi trả về (Response) ---
    // Client không cần gửi lên các trường này, Server tự set
    private String id;            // ID tin nhắn (để client làm key render list)
    private String senderName;    // Tên người gửi (để hiển thị UI)
    private String senderAvatar;  // Avatar người gửi
    private Boolean isRead;       // Trạng thái đã xem
    private String createdAt;     // Thời gian tạo (Nên trả về String ISO-8601 để Client dễ parse)
}