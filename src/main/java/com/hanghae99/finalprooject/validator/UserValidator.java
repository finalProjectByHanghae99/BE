package com.hanghae99.finalprooject.validator;

import com.hanghae99.finalprooject.dto.SignupDto;
import com.hanghae99.finalprooject.exception.ErrorCode;
import com.hanghae99.finalprooject.exception.PrivateException;

import java.util.regex.Pattern;

public class UserValidator {
    public static void validateUserRegister(SignupDto.RequestDto requestDto) {

        String email = requestDto.getEmail();
        String password = requestDto.getPassword();
        String pwCheck = requestDto.getPwCheck();
        String nickname = requestDto.getNickname();

        String patternEmail = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$";
        String patternPw = "^[A-Za-z0-9]{6,20}$";
        String patternNickname = "^[A-Za-z0-9가-힣]{4,10}$";

        // 이메일 설정 유효성 검사
        if (email == null || !Pattern.matches(patternEmail, email)) {
            throw new PrivateException(ErrorCode.DUPLICATE_ERROR_SIGNUP_EMAIL);
        }

        // 비밀번호 설정 유효성 검사
        if (password == null || !Pattern.matches(patternPw, password)) {
            throw new PrivateException(ErrorCode.WRONG_INPUT_SIGNUP_PASSWORD);
        }

        // 비밀번호 확인 유효성 검사
        if (!password.equals(pwCheck)) {
            throw new PrivateException(ErrorCode.WRONG_INPUT_SIGNUP_PWCHECK);
        }

        // 닉네임 설정 유효성 검사
        if (nickname == null || !Pattern.matches(patternNickname, nickname)) {
            throw new PrivateException(ErrorCode.WRONG_INPUT_SIGNUP_NICKNAME);
        }
    }
}