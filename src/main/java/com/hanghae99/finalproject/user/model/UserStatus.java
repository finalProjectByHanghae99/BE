package com.hanghae99.finalproject.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatus {
    USER_STATUS_TEAM_STARTER("starter"), // 모집 글 쓴 유저
    USER_STATUS_APPLICANT("applicant"),  // 신청 한 유저
    USER_STATUS_MEMBER("member"),  // 신청 후 수락된 유저
    USER_STATUS_USER("user"), // 신청 안한 유저
    USER_STATUS_ANONYMOUS("anonymous"); // 로그인 전

    private String userStatus;
}