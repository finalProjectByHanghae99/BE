package com.hanghae99.finalproject.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfo {

    private Long userId;
    private boolean isProfileSet;
}