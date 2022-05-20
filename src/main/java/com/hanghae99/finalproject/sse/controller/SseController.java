package com.hanghae99.finalproject.sse.controller;


import com.hanghae99.finalproject.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Slf4j
@RestController
public class SseController {
    public static Map<Long, SseEmitter> sseEmitters = new ConcurrentHashMap<>();
    private final JwtTokenProvider tokenProvider;

    @GetMapping(value = "/sub" , consumes = MediaType.ALL_VALUE)
    public SseEmitter subscribe(@RequestParam(value = "token") String token){

        log.info("token : {}",token);
        Long userId = tokenProvider.getUserIdFromToken(token);

        log.info("userId : {}",userId);


        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
        try{
            //연결
            sseEmitter.send(SseEmitter.event().name("connect"));


        }catch (IOException e){
            e.printStackTrace();
        }

        //userId pk 값을 key값으로 저장해서 SseEmitter 저장
        sseEmitters.put(userId,sseEmitter);

        //사용자별로 SseEmitter를 식별하여 이벤트를 보낸다 .
        sseEmitter.onCompletion(()->sseEmitters.remove(userId));
        sseEmitter.onTimeout(()-> sseEmitters.remove(userId));
        sseEmitter.onError((e) -> sseEmitters.remove(userId));

        return sseEmitter;

    }

}
