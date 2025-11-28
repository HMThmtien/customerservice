package com.orderapp.customerservice.entity.shareddb;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @UuidGenerator // Tự động sinh UUID v4 nếu id null
    @Column(name = "id", updatable = false, nullable = false, length = 36)
    private String id;

    @Column(name = "room_id", nullable = false, length = 100)
    private String roomId;

    // --- Sender Info ---
    @Column(name = "sender_id", nullable = false, length = 36)
    private String senderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "sender_type", nullable = false, length = 20)
    private UserType senderType;

    // --- Receiver Info ---
    @Column(name = "receiver_id", nullable = false, length = 36)
    private String receiverId;

    @Enumerated(EnumType.STRING)
    @Column(name = "receiver_type", nullable = false, length = 20)
    private UserType receiverType;

    // --- Content ---
    // Sử dụng columnDefinition = "TEXT" để PostgreSQL hiểu đây là kiểu Text dài (không giới hạn 255 ký tự)
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", length = 20)
    @Builder.Default // Lombok Builder sẽ dùng giá trị mặc định này
    private MessageType messageType = MessageType.TEXT;

    @Column(name = "attachment_url", columnDefinition = "TEXT")
    private String attachmentUrl;

    // --- Status ---
    @Column(name = "is_read")
    @Builder.Default
    private Boolean isRead = false;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    // --- Timestamps ---
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // --- Inner Enums Definition ---

    // Định nghĩa loại User (dựa trên schema cũ của bạn)
    public enum UserType {
        CUSTOMER,
        PARTNER,
        SYSTEM,
        DRIVER,
        ADMIN
    }

    // Định nghĩa loại tin nhắn
    public enum MessageType {
        TEXT,
        IMAGE,
        FILE,
        LOCATION
    }

    public Message(String roomId, String senderId, UserType senderType, String receiverId, UserType receiverType, String content) {
        this.roomId = roomId;
        this.senderId = senderId;
        this.senderType = senderType;
        this.receiverId = receiverId;
        this.receiverType = receiverType;
        this.content = content;
        this.isRead = false;
        this.messageType = MessageType.TEXT;
        // ID và CreatedAt để Hibernate tự lo
    }
}
