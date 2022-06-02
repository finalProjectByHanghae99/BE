package com.hanghae99.finalproject.sse.model;


import com.hanghae99.finalproject.common.timeConversion.TimeStamped;
import com.hanghae99.finalproject.user.model.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class Notification extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @Embedded
    private NotificationContent content;
    //알림 내용 - 50자 이내

    @Embedded
    private RelatedURL url;
    //관련 링크 - 클릭 시 이동해야할 링크

    @Column(nullable = false)
    private Boolean isRead;
    //읽었는지에 대한 여부

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType;
    // 알림 종류 [신청 / 수락 / 거절 등등 ]

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action= OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id")
    private User receiver;
    //회원정보

    @Builder
    public Notification(User receiver, NotificationType notificationType, String content, String url, Boolean isRead) {
        this.receiver = receiver;
        this.notificationType = notificationType;
        this.content = new NotificationContent(content);
        this.url = new RelatedURL(url);
        this.isRead = isRead;
    }

    public void read() {
        isRead = true;
    }

    public String getContent() {
        return content.getContent();
    }

    public String getUrl() {
        return url.getUrl();
    }

}

/*
 알림 기능 처리 요소
  - 누구 : ~ 에 대한 알림이 도착했다. 형식의 알림을 클릭하면 관련 페이지로 이동하는 방식.
  - 알림을 읽으면 '읽음' 처리가 되어야한다.

 */
