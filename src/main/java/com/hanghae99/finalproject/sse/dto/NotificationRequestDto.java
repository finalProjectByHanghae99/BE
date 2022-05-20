package com.hanghae99.finalproject.sse.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NotificationRequestDto {

    String receiver;
    String NotificationType;
    String content;
    String url;

}
