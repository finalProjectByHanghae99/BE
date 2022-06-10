package com.hanghae99.finalproject.sseTest;


import com.hanghae99.finalproject.sse.model.Notification;
import com.hanghae99.finalproject.sse.model.NotificationType;
import com.hanghae99.finalproject.sse.service.NotificationService;
import com.hanghae99.finalproject.user.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class EmitterServiceTest {

    @Autowired
    NotificationService notificationService;


    @Test
    @DisplayName("알림 구독을 진행한다.")
    public void subscribe() throws Exception {
        //given
        Long userId = 5L;
        User user = new User(userId);
        String lastEventId = "";

        //when, then
        Assertions.assertDoesNotThrow(() -> notificationService.subscribe(user.getId(), lastEventId));
    }

    @Test
    @DisplayName("알림 메세지를 전송한다.")
    public void send() throws Exception {
        //given
        Long userId = 6L;
        User user = new User(userId);
        String lastEventId = "";
        notificationService.subscribe(userId, lastEventId);

        //when, then
        Assertions.assertDoesNotThrow(() -> notificationService.send(user, NotificationType.APPLY, "''님이 신청에 지원하셨습니다.", "link"));
    }


}
