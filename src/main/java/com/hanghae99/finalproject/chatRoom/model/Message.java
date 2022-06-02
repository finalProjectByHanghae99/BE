package com.hanghae99.finalproject.chatRoom.model;

import com.hanghae99.finalproject.chatRoom.dto.MessageDto;
import com.hanghae99.finalproject.chatRoom.repository.RoomRepository;
import com.hanghae99.finalproject.common.exception.ErrorCode;
import com.hanghae99.finalproject.common.exception.CustomException;
import com.hanghae99.finalproject.common.timeConversion.TimeStamped;
import com.hanghae99.finalproject.user.model.User;
import com.hanghae99.finalproject.user.repository.UserRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
public class Message extends TimeStamped {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String content; // 메시지 내용

    @Enumerated(EnumType.STRING)
    private MessageType messageType; // 메시지 타입

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="user_id", nullable = false)
    private User user; // 메시지를 보내는 사람

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room; // 현재 방 정보

    @Builder
    public Message(Long id, User user, String content, MessageType messageType, Room room) {
        this.id = id;
        this.user = user;
        this.content = content;
        this.messageType = messageType;
        this.room = room;
    }

    @Builder
    public Message(MessageDto sendMessageDto, UserRepository userRepository, RoomRepository roomRepository) {
        this.messageType = sendMessageDto.getType();
        this.user = userRepository.findById(sendMessageDto.getSenderId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_INFO));
        this.content = sendMessageDto.getMessage();
        this.room = roomRepository.findByRoomName(sendMessageDto.getRoomName()).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 방 입니다."));
    }
    // 메시지타입 : 채팅 퇴장 입장
    public enum MessageType {
        TALK, EXIT, START //대문자 사용
    }
}