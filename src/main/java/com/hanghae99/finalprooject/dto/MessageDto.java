package com.hanghae99.finalprooject.dto;

import com.hanghae99.finalprooject.model.Message;
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
