//package com.hanghae99.finalproject.sse.model;
//
//
//import com.hanghae99.finalproject.timeConversion.TimeStamped;
//import com.hanghae99.finalproject.user.model.User;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import javax.persistence.*;
//
//@Getter
//@Entity
//@NoArgsConstructor
//public class Notification extends TimeStamped {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "notification_id")
//    private Long id;
//
//    @Column
//    private String content;
//    //알림 내용
//
//    @Column
//    private String url;
//    //관련 링크
//
//    @Column(nullable = false)
//    private Boolean isRead;
//    //읽었는지에 대한 여부
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private NotificationType notificationType;
//    // 알림 종류 [신청 / 수락 / 거절 등등 ]
//
//    @ManyToOne(fetch = FetchType.LAZY , cascade = CascadeType.ALL)
//    @JoinColumn(name = "user_id")
//    private User receiver;
//    //회원정보
//
//    @Builder
//    public Notification(User receiver, NotificationType notificationType, String content, String url, Boolean isRead) {
//        this.receiver = receiver;
//        this.notificationType = notificationType;
//        this.content = content;
//        this.url = url;
//        this.isRead = isRead;
//    }
//
//    public enum NotificationType {
//        APPLY, ACCEPT ,REJECT //대문자 사용
//    }
//
//
//
//
//}
