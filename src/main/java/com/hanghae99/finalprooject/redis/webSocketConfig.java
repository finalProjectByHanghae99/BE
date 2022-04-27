package com.hanghae99.finalprooject.redis;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@RequiredArgsConstructor
@Configuration
@EnableWebSocketMessageBroker //Stomp 사용을 위해 선언
public class webSocketConfig implements WebSocketMessageBrokerConfigurer {

    // WebSocketMessageBrokerConfigurer 상속받아 아래 두 메서드를 재정의
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/sub");
        // 메시지 구독 요청의 prefix -> /sub로 시작하도록 설정
        config.setApplicationDestinationPrefixes("/pub");
        //메시지 발행 요청의 prefix -> /pub 로 시작하도록 설정
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/webSocket").setAllowedOriginPatterns("*")
                .withSockJS();
        //엔드포인트[웹 소켓 연결 시 패스]를 설정 , CORS 허용 ,
        //.withSockJS() 브라우저에서 웹소켓 지원 x -> fallback 옵션 활성화

    }



}
