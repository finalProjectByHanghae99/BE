package com.hanghae99.finalproject.sse.eventlistener;


import com.hanghae99.finalproject.sse.dto.NotificationRequestDto;
import com.hanghae99.finalproject.sse.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final NotificationService notificationService;

    @TransactionalEventListener
    @Async // 동기적으로 실행하는 부분에 대해서는 해당 어노테이션으로 비동기 처리 가능 ,
    public void handleNotification(NotificationRequestDto requestDto){
        notificationService.send(requestDto.getReceiver(),requestDto.getNotificationType(),
                requestDto.getContent(),requestDto.getUrl());
    }
}

/*

    발행에 대한 이벤트를 리스닝하고 있다가 처리해준다 (알림 전송)
    @EventListner 는 동기적으로 처리 해준다

    모든 동작이 완료된 후에 알림을 보낸다면 ,? -> 알림도 늦게 간다는 것
    순서를 반대로 바꿔 맨 앞으로 보내도 문제다
    -> 도중에 예외가 발생하는 작업이라면?
    이를 위해   @TransactionalEventListener 를 활용해 트랜잭션의 흐름대로 이벤트를 제어

    현재 문제 -> 리스너 적용 시 구독자에게 알림이 가지 않는다..


 */