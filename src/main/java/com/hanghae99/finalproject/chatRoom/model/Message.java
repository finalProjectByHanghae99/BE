package com.hanghae99.finalproject.chatRoom.model;

import com.hanghae99.finalproject.chatRoom.dto.MessageDto;
import com.hanghae99.finalproject.chatRoom.repository.RoomRepository;
import com.hanghae99.finalproject.timeConversion.TimeStamped;
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

    //대화 상태
    public enum MessageType {
        Talk, Exit, Start
    }

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @Builder
    public Message(MessageDto sendMessageDto, UserRepository userRepository, RoomRepository roomRepository) {
        this.messageType = sendMessageDto.getType();
        this.user = userRepository.findById(sendMessageDto.getSenderId()).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        this.content = sendMessageDto.getMessage();
        this.room = roomRepository.findByRoomName(sendMessageDto.getRoomName()).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 방 입니다."));
    }
}