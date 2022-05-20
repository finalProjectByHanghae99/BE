package com.hanghae99.finalproject.sse.controller;


import com.hanghae99.finalproject.security.UserDetailsImpl;
import com.hanghae99.finalproject.sse.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // MIME TYPE - text/event-stream 형태로 받아야함.
    // 클라이어트로부터 오는 알림 구독 요청을 받는다.
    // 누구로부터 오는 알림인지는 @AuthenticationPrincipal 을 통해 입력받는다.
    @GetMapping(value ="/subscribe" , produces = "text/event-stream")
    public SseEmitter subscribe(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                @RequestHeader(value="Last-Event-ID",required = false,defaultValue = "")
                                String lastEventId){
        return notificationService.subscribe(userDetails.getUser().getId(),lastEventId);
    }



}
