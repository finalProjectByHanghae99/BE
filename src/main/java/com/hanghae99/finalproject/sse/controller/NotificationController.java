package com.hanghae99.finalproject.sse.controller;


import com.hanghae99.finalproject.exception.StatusResponseDto;
import com.hanghae99.finalproject.security.UserDetailsImpl;
import com.hanghae99.finalproject.sse.dto.NotificationCountDto;
import com.hanghae99.finalproject.sse.dto.NotificationDto;
import com.hanghae99.finalproject.sse.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
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

    //전체목록 알림 조회에서 해당 목록 클릭 시 읽음처리 ,
    @PostMapping("/notification/read/{notificationId}")
    public void readNotification(@PathVariable Long notificationId){
        notificationService.readNotification(notificationId);

    }
    //알림 조회 - 구독자가 현재 읽지않은 알림 갯수
    @GetMapping(value = "/notifications/count")
    public NotificationCountDto countUnReadNotifications(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return notificationService.countUnReadNotifications(userDetails.getUser().getId());
    }

    //알림 전체 삭제
    @DeleteMapping(value = "/notifications/delete")
    public ResponseEntity<Object> deleteNotifications(@AuthenticationPrincipal UserDetailsImpl userDetails){

        notificationService.deleteAllByNotifications(userDetails);
        return new ResponseEntity<>(new StatusResponseDto("알림 목록 전체삭제 성공",""), HttpStatus.OK);
    }
    //단일 알림 삭제
    @DeleteMapping(value = "/notifications/delete/{notificationId}")
    public ResponseEntity<Object> deleteNotification(@PathVariable Long notificationId){

        notificationService.deleteByNotifications(notificationId);
        return new ResponseEntity<>(new StatusResponseDto("알림 목록 삭제 성공",""), HttpStatus.OK);
    }


    /*
        1. count -> 안 읽은 카운트
        2. reset -> 전체목록은 전체목록만 , 읽음처리를 할 수 있는 api가 필요함




        0. 구독 -> 서버로부터 오는 알람 받음

        2.notifications -> 내가 가지고있는 알림 목록을 다불러옴 [불러올 때 애들의 스테이터스 상태는 true가 됨]

        1.notifications/count -> notifications 알람에서 상태가 false 인 친구들을 가져옴
     */








}
