package com.hanghae99.finalproject.user.dto;

import com.hanghae99.finalproject.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class SignupDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestDto {
        private String email;
        private String password;
        private String pwCheck;
        private String nickname;
        private String major;
        private String intro;
        private String profileImg;
    }

    @Getter
    public static class ResponseDto {
        private String nickname;
        private String major;
        private String profileImg;

        public ResponseDto(User user) {
            this.major = user.getMajor();
            this.nickname = user.getNickname();
            this.profileImg = user.getProfileImg();
        }
    }
}