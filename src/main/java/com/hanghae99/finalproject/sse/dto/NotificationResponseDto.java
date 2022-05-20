package com.hanghae99.finalproject.sse.dto;

import com.hanghae99.finalproject.sse.model.Notification;
import com.hanghae99.finalproject.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class NotificationResponseDto {

    User receiver;
    Notification.NotificationType notificationType;
    String content;
    String url;


    public static Object create(Notification notification) {
        return NotificationResponseDto.builder()
                .receiver(notification.getReceiver())
                .content(notification.getContent())
                .url(notification.getUrl())
                .notificationType(notification.getNotificationType())
                .build();
    }
}
