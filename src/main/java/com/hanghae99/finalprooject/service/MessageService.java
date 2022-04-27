package com.hanghae99.finalprooject.service;

import com.hanghae99.finalprooject.dto.MessageDto;
import com.hanghae99.finalprooject.dto.MessageListDto;
import com.hanghae99.finalprooject.dto.RoomDto;
import com.hanghae99.finalprooject.model.*;
import com.hanghae99.finalprooject.redis.RedisMessagePublisher;
import com.hanghae99.finalprooject.repository.*;
import com.hanghae99.finalprooject.security.UserDetailsImpl;
import com.hanghae99.finalprooject.timeConversion.MessageTimeConversion;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class MessageService {

    private final RedisMessagePublisher redisMessagePublisher;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final UserRoomRepository userRoomRepository;
    private final MessageRepository messageRepository;
    private final PostRepository postRepository;

    public void sendMessage(MessageDto messageDto) {
        LocalDateTime now = LocalDateTime.now();
        MessageDto sendMessageDto = new MessageDto();
        boolean check = false;

        User sender = userRepository.findById(messageDto.getSenderId()).orElseThrow(
                () -> new IllegalArgumentException("메시지를 보내는 이가 없습니다.")
        );

        User receiver = userRepository.findById(messageDto.getReceiverId()).orElseThrow(
                () -> new IllegalArgumentException("메시지를 받는 이가 없습니다.")
        );

        // 전달받는 메시지 타입을 체크 ,
        // 메시지 시작.
        if (Message.MessageType.Start.equals(messageDto.getType())) {
            sendMessageDto = MessageDto.builder()
                    .message(sender.getNickname() + "님이 입장 하셨습니다.")
                    .senderId(sender.getId()) //ex : 게시물에 접근한 유저 / 채팅을 시작
                    .roomName(messageDto.getRoomName()) //현재 방
                    .receiverId(messageDto.getReceiverId()) // 게시물 주인  // 채팅을 받는
                    .createdAt(MessageTimeConversion.timeConversion(now)) //채팅 시간
                    .type(messageDto.getType()) //채팅 타입


                    .build();
            // 채팅방을 나갈 시에 메시지
        } else if (Message.MessageType.Exit.equals(messageDto.getType())) {
            sendMessageDto = MessageDto.builder()
                    .message(sender.getNickname() + "님이 퇴장 하셨습니다.")
                    .senderId(sender.getId())
                    .roomName(messageDto.getRoomName())
                    .createdAt(MessageTimeConversion.timeConversion(now))
                    .type(messageDto.getType())
                    .receiverId(messageDto.getReceiverId())

                    .build();

            check = roomOut(sendMessageDto); // 채팅 방 유무 확인

            //채팅
        } else if (Message.MessageType.Talk.equals(messageDto.getType())) {
            sendMessageDto = MessageDto.builder()
                    .message(messageDto.getMessage())
                    .senderId(sender.getId())
                    .roomName(messageDto.getRoomName())
                    .createdAt(MessageTimeConversion.timeConversion(now))
                    .type(messageDto.getType())
                    .receiverId(messageDto.getReceiverId())
                    .build();

        }

        if(!check){  // 메시지 전달 유저가 존재할 시,
            // 채팅방의 유무 확인
            Room room = roomRepository.findByRoomName(sendMessageDto.getRoomName()).orElseThrow(
                    () -> new IllegalArgumentException("해당하는 방이 존재하지 않습니다.")
            );
            // 있다면 ,
            // 해당 유저의 채팅방에서
            List<UserRoom> userRoomList = userRoomRepository.findByRoom(room);

            // 메시지/타입/sender/게시글 작성자 정보 + 유저와 방 repo = 해당 메시지를 저장
            Message message = new Message(sendMessageDto, userRepository, roomRepository);
            messageRepository.save(message);

            for(UserRoom userRoom : userRoomList){
                //메시지를 보내는 pk 를 해당 방의 마지막 메시지를 보낸 Id로 변경
                userRoom.lastMessageIdChange(message.getId());

            }

            // pub -> 메시지를 방에 전달
            redisMessagePublisher.publish(sendMessageDto);
        }
    }

    // 유저가 방을 나간다면
    public boolean roomOut(MessageDto sendMessageDto) {

        Room room = roomRepository.findByRoomName(sendMessageDto.getRoomName()).orElseThrow(
                () -> new IllegalArgumentException("해당 방이 존재하지 않습니다.")
        );

        List<UserRoom> userRoomList = userRoomRepository.findByRoom(room);

        //자신의 USERROOM 삭제
        for(UserRoom userRoom : userRoomList){
            //현재 게시글에 접근한 유저와 해당 방의 userRoom의 pk값이 같다면 방을 삭제.
            if(userRoom.getUser().getId() == sendMessageDto.getSenderId()){
                userRoomRepository.deleteById(userRoom.getId());
            }
        }



        if(userRoomList.size() == 1 ){ // 마지막으로 나가는 사람 -> room 삭제
            messageRepository.deleteAllByRoom(room);
            roomRepository.deleteById(room.getId());
            return true;
        }

        return false;
    }


    @Transactional
    public MessageListDto showMessageList(RoomDto.findRoomDto roomDto, Pageable pageable, UserDetailsImpl userDetails){


        Room room = userRoomCount(roomDto);

        //해당 RoomPostId로 게시물 존재 확인 -> 조회/ 없다면 Null
        Post post =  postRepository.findById(room.getRoomPostId()).orElse(null);

        //특정 방에 해당하는 메시지 정보 가져오기
        PageImpl<Message> messages = messageRepository.findByRoom(room,pageable);

        // 게시물 작성자 입장 / 게시물에 접근한 유저 입장에서 보낸 메시지 인지 판별
        List<MessageDto> messageDtos = DiscriminationWhoSentMessage(roomDto, userDetails, room, messages);


        return MessageListDto.builder()
                .message(messageDtos)
                .build();
    }

    private Room userRoomCount(RoomDto.findRoomDto roomDto){
        //메시지를 담고 있을 방을 찾는다.
        Room room = roomRepository.findByRoomNameAndRoomPostId(roomDto.getRoomName(), roomDto.getPostId()).orElseThrow(
                () -> new IllegalArgumentException("해당 방이 존재하지 않습니다."));
        //게시물 작성 유저의 정보 조회
        User user = userRepository.findById(roomDto.getUserId()).orElseThrow(
                () -> new IllegalArgumentException("해당 유저 없음")
        );
        UserRoom userRoom = userRoomRepository.findByRoomAndUser(room,user);
        //해당 유저방에 카운팅 -> 0
        userRoom.countInit();
        return room;

    }

    private List<MessageDto> DiscriminationWhoSentMessage(RoomDto.findRoomDto roomDto , UserDetailsImpl userDetails
            ,Room room, PageImpl<Message> messages){
        List<MessageDto> messageDtos = new ArrayList<>();
        for(Message message : messages){
            //게시물 주인이 보낸 메시지
            if(roomDto.getToUserId() == message.getUser().getId()){
                MessageDto messageDto = MessageDto.builder()
                        .message(message.getContent())
                        .roomName(room.getRoomName())
                        .senderId(message.getUser().getId())
                        // .receiverId(userDetails.getUser().getId())
                        .type(message.getMessageType())
                        .createdAt(MessageTimeConversion.timeConversion(message.getCreateAt()))
                        .build();
                messageDtos.add(messageDto);
            }else{
                //게시물에 접근한 유저가 보낸 메시지
                MessageDto messageDto = MessageDto.builder()
                        .message(message.getContent())
                        .roomName(room.getRoomName())
                        .senderId(message.getUser().getId())
                        .receiverId(roomDto.getToUserId())
                        .type(message.getMessageType())
                        .createdAt(MessageTimeConversion.timeConversion(message.getCreateAt()))
                        .build();
                messageDtos.add(messageDto);
            }
        }
        return messageDtos;

    }


    @Transactional
    public void updateRoomMessageCount(RoomDto.UpdateCountDto updateCountDto) {
        Room room = roomRepository.findByRoomName(updateCountDto.getRoomName()).orElseThrow(
                () -> new IllegalArgumentException("해당 방이 존재하지 않습니다.")
        );

        User user = userRepository.findById(updateCountDto.getUserId()).orElseThrow(
                () -> new IllegalArgumentException("해당 유저가 없습니다.")
        );

        UserRoom userRoom = userRoomRepository.findByRoomAndUser(room, user);

        userRoom.countChange();
    }
}


