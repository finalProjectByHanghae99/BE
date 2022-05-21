package com.hanghae99.finalproject.sse.controller;


import com.hanghae99.finalproject.security.UserDetailsImpl;
import com.hanghae99.finalproject.sse.dto.NotificationDto;
import com.hanghae99.finalproject.sse.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NotificationController {


    private final NotificationService notificationService;

    // MIME TYPE - text/event-stream 형태로 받아야함.
    // 클라이어트로부터 오는 알림 구독 요청을 받는다.
    // 로그인한 유저는 SSE 연결
    // lAST_EVENT_ID = 이전에 받지 못한 이벤트가 존재하는 경우 [ SSE 시간 만료 혹은 종료 ]
    // 전달받은 마지막 ID 값을 넘겨 그 이후의 데이터[ 받지 못한 데이터 ]부터 받을 수 있게 한다
    @GetMapping(value ="/subscribe" , produces = "text/event-stream")
    public SseEmitter subscribe(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                @RequestHeader(value="Last-Event-ID",required = false,defaultValue = "")
                                String lastEventId){
        return notificationService.subscribe(userDetails.getUser().getId(),lastEventId);
    }


    //알림조회
    @GetMapping(value = "/notifications")
    public List<NotificationDto> findAllNotifications(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return notificationService.findAllNotifications(userDetails.getUser().getId());
    }


}
