package com.hanghae99.finalproject.sse.dto;


import com.hanghae99.finalproject.sse.model.NotificationType;
import com.hanghae99.finalproject.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequestDto {
    private User receiver;
    private NotificationType notificationType;
    private String content;
    private String url;


}