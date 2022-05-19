package com.hanghae99.finalproject.chatRoom.service;

import com.hanghae99.finalproject.chatRoom.dto.MessageDto;
import com.hanghae99.finalproject.chatRoom.dto.MessageListDto;
import com.hanghae99.finalproject.chatRoom.dto.RoomDto;
import com.hanghae99.finalproject.chatRoom.model.Message;
import com.hanghae99.finalproject.chatRoom.model.Room;
import com.hanghae99.finalproject.chatRoom.model.UserRoom;
import com.hanghae99.finalproject.chatRoom.repository.MessageRepository;
import com.hanghae99.finalproject.chatRoom.repository.RoomRepository;
import com.hanghae99.finalproject.chatRoom.repository.UserRoomRepository;
import com.hanghae99.finalproject.post.model.Post;
import com.hanghae99.finalproject.post.repository.PostRepository;
import com.hanghae99.finalproject.redis.RedisMessagePublisher;
import com.hanghae99.finalproject.security.UserDetailsImpl;
import com.hanghae99.finalproject.timeConversion.MessageTimeConversion;
import com.hanghae99.finalproject.user.model.User;
import com.hanghae99.finalproject.user.repository.UserRepository;
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
                () -> new IllegalArgumentException("해당되는 receiver없음")
        );


        // 전달 메시지 타입을 체크 ,
        // 메시지 시작.
        if (Message.MessageType.START.equals(messageDto.getType())) {
            sendMessageDto = MessageDto.builder()
                    .message(sender.getNickname() + "님이 입장 하셨습니다.")
                    .senderId(sender.getId()) //ex : 게시물에 접근한 유저 / 채팅을 시작
                    .roomName(messageDto.getRoomName()) //현재 방
                    .receiverId(messageDto.getReceiverId()) // 게시물 주인  // 채팅을 받는
                    .createdAt(MessageTimeConversion.timeConversion(now)) //채팅 시간
                    .type(messageDto.getType()) //채팅 타입


                    .build();
            // 채팅방을 나갈 시에 메시지
        } else if (Message.MessageType.EXIT.equals(messageDto.getType())) {
            sendMessageDto = MessageDto.builder()
                    .message(sender.getNickname() + "님이 퇴장 하셨습니다.")
                    .senderId(sender.getId())
                    .roomName(messageDto.getRoomName())
                    .createdAt(MessageTimeConversion.timeConversion(now))
                    .type(messageDto.getType())
                    .receiverId(messageDto.getReceiverId())

                    .build();

            check = roomOut(sendMessageDto); // roomOut ->true 반환 시

            //채팅
        } else if (Message.MessageType.TALK.equals(messageDto.getType())) {
            sendMessageDto = MessageDto.builder()
                    .message(messageDto.getMessage())
                    .senderId(sender.getId())
                    .roomName(messageDto.getRoomName())
                    .createdAt(MessageTimeConversion.timeConversion(now))
                    .type(messageDto.getType())
                    .receiverId(messageDto.getReceiverId())
                    .build();

        }

        if(check == false){
            // TALK / START
            Room room = roomRepository.findByRoomName(sendMessageDto.getRoomName()).orElseThrow(
                    () -> new IllegalArgumentException("해당하는 방이 존재하지 않습니다.")
            );

            // 방을 공유하는 유저룸 리스트를 가져온다.
            List<UserRoom> userRoomList = userRoomRepository.findByRoom(room);

            // 메시지/타입/sender/게시글 작성자 정보 + 유저와 방 repo = 해당 메시지를 저장
            Message message = new Message(sendMessageDto, userRepository, roomRepository);
            messageRepository.save(message);

            for(UserRoom userRoom : userRoomList){
                //메시지를 보내는 pk 를 두 개의 userRoom의 마지막 메시지를 보낸 Id로 변경
                userRoom.lastMessageIdChange(message.getId());

            }

            // pub -> 채널 구독자에게 전달
            redisMessagePublisher.publish(sendMessageDto);
        }
    }

    // 유저가 방을 나간다면
    public boolean roomOut(MessageDto sendMessageDto) {
        //현재 방을 찾는다.
        Room room = roomRepository.findByRoomName(sendMessageDto.getRoomName()).orElseThrow(
                () -> new IllegalArgumentException("해당 방이 존재하지 않습니다.")
        );
        //방을 찾아와 유저룸 리스트를 반환
        List<UserRoom> userRoomList = userRoomRepository.findByRoom(room);

        //현재 유저룸 리스트에서 자신의 유저룸 삭제
        for(UserRoom userRoom : userRoomList){
            //현재 게시글에 접근한 유저와 해당 방의 userRoom의 pk값이 같다면 방을 삭제.
            if(userRoom.getUser().getId() == sendMessageDto.getSenderId()){
                userRoomRepository.deleteById(userRoom.getId());
            }
        }
        // 반대편 유저룸에서는 메시지를 삭제 하고 , 마지막으로 방정보도 같이 삭제하여준다.
        if(userRoomList.size() == 1 ){
            messageRepository.deleteAllByRoom(room);
            roomRepository.deleteById(room.getId());
            return true;
        }

        return false;
    }


    //방의 메시지 리스트를 조회  / 방 이름, 모집글 PK , 유저 PK , 상대방 PK , 내 정보
    @Transactional
    public MessageListDto showMessageList(RoomDto.findRoomDto roomDto, Pageable pageable, UserDetailsImpl userDetails){

        //리스트 조회시 userRoom 카운트 초기화
        Room room = userRoomCount(roomDto);

        //특정 방에 해당하는 메시지 정보 가져오기
        PageImpl<Message> messages = messageRepository.findByRoom(room,pageable);

        //메시지를 리스트에 담아준다 [나와 상대방의 채팅 리스트를 나눈다 ]
        List<MessageDto> messageDtos = DiscriminationWhoSentMessage(roomDto, userDetails, room, messages);

        // 메시지가 담긴 리스트 반환 ,
        return MessageListDto.builder()
                .message(messageDtos)
                .build();//
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
                        .receiverId(userDetails.getUser().getId())
                        .type(message.getMessageType())
                        .createdAt(MessageTimeConversion.timeConversion(message.getCreatedAt()))
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
                        .createdAt(MessageTimeConversion.timeConversion(message.getCreatedAt()))
                        .build();
                messageDtos.add(messageDto);
            }
        }
        return messageDtos;

    }

    private Room userRoomCount(RoomDto.findRoomDto roomDto){
        // 방 이름, 모집글 PK , 유저 PK , 상대방 PK
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

    //
    @Transactional
    public void updateRoomMessageCount(RoomDto.UpdateCountDto updateCountDto) {
        Room room = roomRepository.findByRoomName(updateCountDto.getRoomName()).orElseThrow(
                () -> new IllegalArgumentException("해당 방이 존재하지 않습니다.")
        );
        //게시물 주인 유저 정보 조회
        User user = userRepository.findById(updateCountDto.getUserId()).orElseThrow(
                () -> new IllegalArgumentException("해당 유저가 없습니다.")
        );

        UserRoom userRoom = userRoomRepository.findByRoomAndUser(room, user);

        userRoom.countChange();
    }
}