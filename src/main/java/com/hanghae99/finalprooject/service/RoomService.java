package com.hanghae99.finalprooject.service;

import com.hanghae99.finalprooject.dto.ChatRoomDto;
import com.hanghae99.finalprooject.dto.ChatUserDto;
import com.hanghae99.finalprooject.dto.LastMessageDto;
import com.hanghae99.finalprooject.dto.RoomDto;
import com.hanghae99.finalprooject.model.*;
import com.hanghae99.finalprooject.repository.*;
import com.hanghae99.finalprooject.security.UserDetailsImpl;
import com.hanghae99.finalprooject.timeConversion.MessageTimeConversion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final UserRoomRepository userRoomRepository;
    private final MessageRepository messageRepository;

    @Transactional
    public RoomDto.Response createRoomService(RoomDto.Request roomDto, UserDetailsImpl userDetails) {

        Post post = postRepository.findById(roomDto.getPostId()).orElseThrow(

                ()-> new IllegalArgumentException("해당 게시글이 존재하지 않아 방을 생성할 수 없습니다.")

        );


        User user = userDetails.getUser(); //게시물에 접근하는 유저

        User toUser = userRepository.findById(roomDto.getToUserId()).orElseThrow( //게시물 주인
                () -> new IllegalArgumentException("no touser")
        );
        List<Room> checkRoomList = roomRepository.findByRoomPostId(post.getId());
        //해당 게시물에 모든 채팅정보 가져오기
        //룸네임 / 룸pk / roomPostId
        for (Room room : checkRoomList) {
            UserRoom checkUserRoom = userRoomRepository.findByRoomAndUserAndToUser(room, user, toUser);
            //해당 게시물에 유저와, 게시물에 접근하는 유저에 채팅방 정보가 있는지 확인
            if (checkUserRoom != null) { //이미 대화하고 있던 방이 있을경우.
                throw new IllegalArgumentException("same room");
            }
        }

        /* 대화하고 있던 방이 없다면 여기부터 새로운 방정보 생성*/
        String roomName = UUID.randomUUID().toString();

        //방생성
        Room room = Room.builder()
                .roomName(roomName) //방이름
                .roomPostId(post.getId()) // 방이 생성된 게시글 ID
                .build();

        roomRepository.save(room); //룸에 저장

        // userRoom two create

        //게시물 주인 입장에서 보이는 채팅방 생성 toUser
        UserRoom userRoom = UserRoom.builder()
                .room(room)
                .user(user)
                .toUser(toUser)
                .lastMessageId(null)
                .count(0)
                .build();

        userRoomRepository.save(userRoom);

        //게시물에 접근한 유저 입장에서 보이는 채팅방 생성
        UserRoom toUserRoom = UserRoom.builder()
                .room(room)
                .user(toUser)
                .toUser(user)
                .lastMessageId(null)
                .count(0)
                .build();
        userRoomRepository.save(toUserRoom);

        ChatUserDto chatUserDto = ChatUserDto.builder()
                .userId(toUser.getId())
                .profileImg(toUser.getProfileImg())
                .nickname(toUser.getNickname())
                .build();

        RoomDto.Response response = RoomDto.Response.builder()
                .roomName(room.getRoomName())
                .user(chatUserDto)
                .build();

        return response;
    }

    /*
    roomName;
    postId;
    ChatUserDto user;
    LastMessageDto lastMessage;
    CurrentState currentState;
    notReadingMessageCount;

     */

    public List<ChatRoomDto> showRoomListService(UserDetailsImpl userDetails) {
        List<UserRoom> userRooms = userRoomRepository.findByUser(userDetails.getUser());
        List<ChatRoomDto> chatRoomDtos = new ArrayList<>();
        for (UserRoom userRoom : userRooms) {
            LastMessageDto lastMessageDto;

            ChatUserDto chatUserDto = ChatUserDto.builder()
                    .userId(userRoom.getToUser().getId())
                    .profileImg(userRoom.getToUser().getProfileImg())
                    .nickname(userRoom.getToUser().getNickname())
                    .build();

            if (userRoom.getLastMessageId() == null) {
                lastMessageDto = LastMessageDto.builder()
                        .content("방이 생성 되었습니다.")
                        .createdAt(MessageTimeConversion.timeConversion(userRoom.getCreateAt()))
                        .build();
            } else {
                Message message = messageRepository.getById(userRoom.getLastMessageId());
                lastMessageDto = LastMessageDto.builder()
                        .content(message.getContent())
                        .createdAt(MessageTimeConversion.timeConversion(message.getCreateAt()))
                        .build();
            }

            Post post = postRepository.findById(userRoom.getRoom().getRoomPostId()).orElse(null);
            ChatRoomDto chatRoomDto;

            if (post == null) {
                chatRoomDto = ChatRoomDto.builder()
                        .roomName(userRoom.getRoom().getRoomName())
                        .postId(userRoom.getRoom().getRoomPostId())
                        .user(chatUserDto)
                        .lastMessage(lastMessageDto)
                        .currentStatus(CurrentStatus.success)
                        .notReadingMessageCount(userRoom.getCount())
                        .build();
            } else {
                chatRoomDto = ChatRoomDto.builder()
                        .roomName(userRoom.getRoom().getRoomName())
                        .postId(userRoom.getRoom().getRoomPostId())
                        .user(chatUserDto)
                        .lastMessage(lastMessageDto)
                        .currentStatus(post.getStatus())
                        .notReadingMessageCount(userRoom.getCount())
                        .build();
            }
            chatRoomDtos.add(chatRoomDto);
        }
        return chatRoomDtos;
    }
}