package com.hanghae99.finalprooject.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomDto {

    // userId 보내야됨
    @Getter
    @Setter
    public static class findRoomDto{
        private String roomName;
        private Long postId;
        private Long userId;
        private Long toUserId;

    }
    // userId 보내야됨
    @Getter
    @Setter
    public static class UpdateCountDto{
        private String roomName;
        private Long userId;
    }

    @Getter
    @Setter
    public static class Request{
        private Long postId;
        private Long toUserId;
    }


    @Getter
    @Setter
    @Builder
    public static class Response{
        private ChatUserDto user;
        private String roomName;
    }
}
