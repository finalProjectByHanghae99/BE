//package com.hanghae99.finalproject.sseTest;
//
//import com.hanghae99.finalproject.sse.model.Notification;
//import com.hanghae99.finalproject.sse.repository.EmitterRepository;
//import com.hanghae99.finalproject.sse.repository.EmitterRepositoryImpl;
//import com.hanghae99.finalproject.user.model.User;
//import lombok.RequiredArgsConstructor;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
//
//import java.awt.*;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@RequiredArgsConstructor
//class EmitterRepositoryImplTest {
//    private final EmitterRepository emitterRepository = new EmitterRepositoryImpl();
//    private final Long DEFAULT_TIMEOUT = 60L * 1000L * 60L;
//
//
//
//    @Test
//    @DisplayName("새로운 emitter를 추가")
//    void save() throws Exception {
//        //given
//        Long userId = 1L;
//        String emitterId = userId +"_"+ System.currentTimeMillis();
//        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
//
//        //when/ then
//        Assertions.assertDoesNotThrow(() -> emitterRepository.save(emitterId,sseEmitter));
//
//    }
//
//    @Test
//    @DisplayName("수신한 이벤트를 캐시에 저장")
//    void saveEventCache() throws Exception{
//        //given
//        Long userId = 1L;
//        String eventCacheId = userId +"_"+ System.currentTimeMillis();
//        Notification notification = new Notification(
//                new User(1L),
//                Notification.NotificationType.APPLY,
//                "상대방으로부터 신청이 왔습니다."
//                ,"url",
//                false);
//
//        //when , then
//        Assertions.assertDoesNotThrow(() -> emitterRepository.saveEventCache(eventCacheId,notification));
//
//    }
//
//    @Test
//    @DisplayName("어떤 회원이 접속한 모든 Emitter를 찾는다")
//    void findAllEmitterStartWithByUserId() throws Exception {
//        //given
//        Long userId = 1L;
//        String emitterId1 = userId + "_" + System.currentTimeMillis();
//        emitterRepository.save(emitterId1,new SseEmitter(DEFAULT_TIMEOUT));
//
//        Thread.sleep(100);
//        String emitterId2 = userId + "_" + System.currentTimeMillis();
//        emitterRepository.save(emitterId2, new SseEmitter(DEFAULT_TIMEOUT));
//
//        Thread.sleep(100);
//        String emitterId3 = userId + "_" + System.currentTimeMillis();
//        emitterRepository.save(emitterId3, new SseEmitter(DEFAULT_TIMEOUT));
//
//        //when
//        Map<String, SseEmitter> ActualResult = emitterRepository.findAllEmitterStartWithByUserId(String.valueOf(userId));
//
//        //then
//        Assertions.assertEquals(3, ActualResult.size());
//    }
//
//    @Test
//    @DisplayName("수신한 이벤트를 캐시에 저장한다.")
//    void findAllEventCacheStartWithByUserId() throws Exception{
//        //given
//        Long memberId = 1L;
//        String eventCacheId1 =  memberId + "_" + System.currentTimeMillis();
//        Notification notification1 = new Notification(new User(1L), Notification.NotificationType.APPLY, "신청이 왔습니다.", "url", false);
//        emitterRepository.saveEventCache(eventCacheId1, notification1);
//
//        Thread.sleep(100);
//        String eventCacheId2 =  memberId + "_" + System.currentTimeMillis();
//        Notification notification2 = new Notification(new User(1L), Notification.NotificationType.ACCEPT, "신청이 승인되었습니다.", "url", false);
//        emitterRepository.saveEventCache(eventCacheId2, notification2);
//
//        Thread.sleep(100);
//        String eventCacheId3 =  memberId + "_" + System.currentTimeMillis();
//        Notification notification3 = new Notification(new User(1L), Notification.NotificationType.REJECT, "신청이 거절되었습니다.", "url", false);
//        emitterRepository.saveEventCache(eventCacheId3, notification3);
//
//        //when
//        Map<String, Object> ActualResult = emitterRepository.findAllEventCacheStartWithByUserId(String.valueOf(memberId));
//
//        //then
//        Assertions.assertEquals(3, ActualResult.size());
//    }
//
//
//    @Test
//    void deleteById() {
//    }
//
//    @Test
//    void deleteAllEmitterStartWithId() {
//    }
//
//    @Test
//    void deleteAllEventCacheStartWithId() {
//    }
//}