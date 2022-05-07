package com.hanghae99.finalproject.user.dto;

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
    @Builder
    @AllArgsConstructor
    public static class ResponseDto {
        private String email;
        private String password;
    }
}