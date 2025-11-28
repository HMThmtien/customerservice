package com.orderapp.customerservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint để Client connect vào (VD: ws://localhost:8080/ws-chat)
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*") // Cho phép CORS từ App React Native/Web
                .withSockJS(); // Fallback nếu không có WS
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");

        // Dùng ActiveMQ thay vì RabbitMQ
        registry.enableStompBrokerRelay("/topic", "/queue")
                .setRelayHost("localhost")
                .setRelayPort(61613)       // Cổng STOMP của ActiveMQ (giống RabbitMQ)
                .setClientLogin("admin")   // User mặc định của ActiveMQ
                .setClientPasscode("admin"); // Pass mặc định của ActiveMQ
    }
}
