package com.hanghae99.finalprooject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class SignupDto {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class RequestDto {
        private String email;
        private String password;
        private String pwCheck;
        private String nickname;
        private String address;
        private String intro;
        private String profileImg;
    }
}