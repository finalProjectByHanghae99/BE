package com.hanghae99.finalproject.user.dto;

import lombok.Getter;

@Getter
public class SignupRequestDto {

    private Long userId;
    private String memberId;
    private String password;
    private String pwCheck;
    private String profileImg;
    private String nickname;
    private String major;
}