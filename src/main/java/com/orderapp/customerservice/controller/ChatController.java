package com.orderapp.customerservice.controller;

import com.orderapp.customerservice.dto.ChatMessageDto;
import com.orderapp.customerservice.entity.shareddb.Message; // <--- SỬA DÒNG NÀY (Trỏ đúng về Entity)
import com.orderapp.customerservice.repository.shareddb.MessageRepository;
import lombok.RequiredArgsConstructor;
// import org.springframework.messaging.Message; <--- XÓA DÒNG NÀY ĐI
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// java.time và UUID ok
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/auth/customer")
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageRepository messageRepository;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageDto chatMessageDto) {

        // Lúc này Message.builder() mới hoạt động vì nó gọi vào Entity Message
        Message message = Message.builder()
                .roomId(chatMessageDto.getRoomId())
                .senderId(chatMessageDto.getSenderId())
                .senderType(Message.UserType.CUSTOMER)
                .receiverId(chatMessageDto.getReceiverId())
                .receiverType(Message.UserType.PARTNER)
                .content(chatMessageDto.getContent())
                // Lưu ý: Đảm bảo ChatMessageDto đã có getter cho 2 trường này
                .messageType(chatMessageDto.getMessageType())
                .attachmentUrl(chatMessageDto.getAttachmentUrl())
                .isRead(false)
                .build();

        // Save xuống DB
        Message savedMessage = messageRepository.save(message);

        // Update DTO trả về
        chatMessageDto.setId(savedMessage.getId());
        // Chuyển LocalDateTime sang String để tránh lỗi format JSON trên Client
        chatMessageDto.setCreatedAt(savedMessage.getCreatedAt().toString());

        // Bắn message
        messagingTemplate.convertAndSend("/topic/room." + chatMessageDto.getRoomId(), chatMessageDto);
    }
}