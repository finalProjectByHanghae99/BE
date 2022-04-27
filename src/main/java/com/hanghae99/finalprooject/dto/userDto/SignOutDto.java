package com.hanghae99.finalprooject.dto.userDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@NoArgsConstructor
public class SignOutDto {
    private String nickname;
}