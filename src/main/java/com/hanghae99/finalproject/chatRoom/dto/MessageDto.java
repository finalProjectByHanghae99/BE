package com.hanghae99.finalproject.chatRoom.dto;

import com.hanghae99.finalproject.chatRoom.model.Message;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MessageDto {

    private Message.MessageType type;
    private String roomName;
    private Long senderId;
    private String message;
    private Long receiverId;
    private String createdAt;
}
