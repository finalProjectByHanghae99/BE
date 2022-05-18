package com.hanghae99.finalproject.chatRoom.service;

import com.hanghae99.finalproject.chatRoom.dto.ChatRoomDto;
import com.hanghae99.finalproject.chatRoom.dto.ChatUserDto;
import com.hanghae99.finalproject.chatRoom.dto.LastMessageDto;
import com.hanghae99.finalproject.chatRoom.dto.RoomDto;
import com.hanghae99.finalproject.chatRoom.model.Message;
import com.hanghae99.finalproject.chatRoom.model.Room;
import com.hanghae99.finalproject.chatRoom.model.UserRoom;
import com.hanghae99.finalproject.chatRoom.repository.MessageRepository;
import com.hanghae99.finalproject.chatRoom.repository.RoomRepository;
import com.hanghae99.finalproject.chatRoom.repository.UserRoomRepository;
import com.hanghae99.finalproject.mail.dto.MailDto;
import com.hanghae99.finalproject.mail.service.MailService;
import com.hanghae99.finalproject.post.model.Post;
import com.hanghae99.finalproject.post.repository.PostRepository;
import com.hanghae99.finalproject.security.UserDetailsImpl;
import com.hanghae99.finalproject.timeConversion.MessageTimeConversion;
import com.hanghae99.finalproject.user.model.User;
import com.hanghae99.finalproject.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
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
    private final MailService mailService;

    @Transactional
    public RoomDto.Response createRoomService(RoomDto.Request roomDto, UserDetailsImpl userDetails) throws MessagingException {

        Post post = postRepository.findById(roomDto.getPostId()).orElseThrow(
                () -> new IllegalArgumentException("해당 게시글이 존재하지 않아 방을 생성할 수 없습니다.")
        );

        User user = userDetails.getUser(); //게시물에 접근하는 유저

        User toUser = userRepository.findById(roomDto.getToUserId()).orElseThrow( //게시물 주인
                () -> new IllegalArgumentException("no touser")
        );

        // 현재 '모집글'의 방 정보 가져오기.
        List<Room> checkRoomList = roomRepository.findByRoomPostId(post.getId());

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

        // 채팅할 유저 정보를 담는 Dto
        ChatUserDto chatUserDto = ChatUserDto.builder()
                .userId(toUser.getId())
                .profileImg(toUser.getProfileImg())
                .nickname(toUser.getNickname())
                .build();


        RoomDto.Response response = RoomDto.Response.builder()
                .roomName(room.getRoomName()) //현재 방
                .user(chatUserDto) //채팅할 유저
                .build();

        // 채팅방 신규 생성시 지원자에게 알림 메일 발송(지원자가 이메일 인증했을 경우만)
        if (user.getIsVerifiedEmail() != null) {
            mailService.chatOnEmailBuilder(MailDto.builder()
                    .toUserId(user.getId())
                    .toEmail(user.getEmail())
                    .toNickname(user.getNickname())
                    .fromNickname(post.getUser().getNickname())
                    .fromProfileImg(post.getUser().getProfileImg())
                    .postId(post.getId())
                    .postTitle(post.getTitle())
                    .build());
        }

        return response;
    }

    /*
    roomName;
    postId;
    ChatUserDto user;
    LastMessageDto lastMessage;
    CurrentState currentState;
    notReadingMessageCount; */

    public List<ChatRoomDto> showRoomListService(UserDetailsImpl userDetails) {
        // 현재 로그인한 유저의 채팅방 목록 리스트를 뽑아낸다.
        List<UserRoom> userRooms = userRoomRepository.findByUser(userDetails.getUser());
        List<ChatRoomDto> chatRoomDtos = new ArrayList<>();
        // 반복을 통해 조건 분기
        for (UserRoom userRoom : userRooms) {
            LastMessageDto lastMessageDto; //마지막 메시지 와 시간

            ChatUserDto chatUserDto = ChatUserDto.builder()
                    .userId(userRoom.getToUser().getId()) // 반대편 유저 pk
                    .profileImg(userRoom.getToUser().getProfileImg()) //반대편 유저의 프로필 이미지
                    .nickname(userRoom.getToUser().getNickname()) //반대편 유저의 닉네임
                    .build();

            if (userRoom.getLastMessageId() == null) { //마지막 메시지가 없다면
                lastMessageDto = LastMessageDto.builder()
                        .content("방이 생성 되었습니다.")  //방이 생성 되었다는 메시지와
                        .createdAt(MessageTimeConversion.timeConversion(userRoom.getCreatedAt()))
                        .build();
            } else {
                // 현재 대화중이라면 , 메시지 pk를 받아와 메시지 정보를 담는다.
                Message message = messageRepository.getById(userRoom.getLastMessageId());
                lastMessageDto = LastMessageDto.builder()
                        .content(message.getContent()) //메시지를 가져와 담는다
                        .createdAt(MessageTimeConversion.timeConversion(message.getCreatedAt()))
                        .build();
            }
            //현재 userRoom pk를 이용하여 모집글 정보를 불러온다.
            Post post = postRepository.findById(userRoom.getRoom().getRoomPostId()).orElse(null);

            ChatRoomDto chatRoomDto;
            //현재 방이름, 모집글 pk , 유저정보 , 마지막메시지 , 카운트 ,
            //모집글이 없더라도 , 채팅방은 남겨둔다.
            if (post == null) {
                chatRoomDto = ChatRoomDto.builder()
                        .roomName(userRoom.getRoom().getRoomName())
                        .postId(userRoom.getRoom().getRoomPostId())
                        .user(chatUserDto)
                        .lastMessage(lastMessageDto)
                        .notReadingMessageCount(userRoom.getCount())
                        .build();
            } else {
                chatRoomDto = ChatRoomDto.builder()
                        .roomName(userRoom.getRoom().getRoomName())
                        .postId(userRoom.getRoom().getRoomPostId())
                        .user(chatUserDto)
                        .lastMessage(lastMessageDto)
                        .notReadingMessageCount(userRoom.getCount())
                        .build();
            }
            chatRoomDtos.add(chatRoomDto);
        }
        return chatRoomDtos;
    }
}