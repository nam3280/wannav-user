package com.ssg.wannavapibackend.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Getter
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${naver.clova.chatbot.secret-key}")
    private String secretKey;

    @Value("${naver.clova.chatbot.invoke-url}")
    private String invokeUrl;

    /**
     * 웹 소캣을 사용하기 위한 설정
     * @param registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // prefix 설정
        registry.setApplicationDestinationPrefixes("/app");

        // topic 이라는 주제에 브로커를 설정
        registry.enableSimpleBroker("/topic");
    }


}
