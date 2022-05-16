package com.hanghae99.finalproject.user.dto;

import lombok.Getter;

@Getter
public class SignupRequestDto {

    private String memberId;
    private String password;
    private String pwCheck;
    private String nickname;
    private String major;
    private String intro;
    private String profileImg;
}