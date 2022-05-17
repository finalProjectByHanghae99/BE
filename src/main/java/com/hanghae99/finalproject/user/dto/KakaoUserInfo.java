package com.hanghae99.finalproject.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KakaoUserInfo {

    private Long id;
    private String kakaoMemberId;
    private boolean isProfileSet;
}