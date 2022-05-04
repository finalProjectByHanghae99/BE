package com.hanghae99.finalproject.redis;

import com.hanghae99.finalproject.chatRoom.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisMessagePublisher {

    /*
        레디스에서는 공통 주제 [Topic] 에 대하여 구독자[sub]에게 메시지를 발행 [pub]
        할 수 있는 기능이 있다.
        통칭하여 sub/ pub 라고 하며 채팅방에서는 redis 의 topic 을 채팅방이라 생각하자!
ff
        pub/ sub 은 대화를 하거나, 보는 행위
 */

    // redis 발행 서비스 구현
    // 메시지 발행 시 redis 구독 서비스가 메시지 처리
    private final RedisTemplate<String,Object> redisTemplate;

    public void publish(MessageDto message){
        redisTemplate.convertAndSend(String.valueOf(message.getRoomName()),message);
    }



}
