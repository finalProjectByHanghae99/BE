package com.hanghae99.finalprooject.dto.userDto;

import com.hanghae99.finalprooject.model.User;
import com.hanghae99.finalprooject.security.jwt.TokenDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class LoginResponseDto {

    private Long userId;
    private String email;
    private String nickname;
    private TokenDto token;

    public static LoginResponseDto of(User user, TokenDto token) {
        return LoginResponseDto.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .token(token)

                .build();
    }

}
