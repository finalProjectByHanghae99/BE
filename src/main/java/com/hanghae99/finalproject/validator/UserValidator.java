package com.hanghae99.finalproject.validator;

import com.hanghae99.finalproject.security.jwt.TokenRequestDto;
import com.hanghae99.finalproject.user.dto.LoginDto;
import com.hanghae99.finalproject.user.dto.SignupDto;
import com.hanghae99.finalproject.exception.ErrorCode;
import com.hanghae99.finalproject.exception.PrivateException;

import java.util.regex.Pattern;

public class UserValidator {
    public static void validateInputEmail(SignupDto.RequestDto requestDto) {

        String email = requestDto.getEmail();

        String patternEmail = "^[a-zA-Z0-9]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$";

        // 이메일 설정 유효성 검사
        if (email == null || !Pattern.matches(patternEmail, email)) {
            throw new PrivateException(ErrorCode.SIGNUP_EMAIL_WRONG_INPUT);
        }
    }

    public static void validateInputPassword(SignupDto.RequestDto requestDto) {

        String password = requestDto.getPassword();
        String pwCheck = requestDto.getPwCheck();

        String patternPw = "^[A-Za-z0-9]{6,20}$";

        // 비밀번호 설정 유효성 검사
        if (password == null || !Pattern.matches(patternPw, password)) {
            throw new PrivateException(ErrorCode.SIGNUP_PASSWORD_WRONG_INPUT);
        }

        // 비밀번호 확인 유효성 검사
        if (!password.equals(pwCheck)) {
            throw new PrivateException(ErrorCode.SIGNUP_PWCHECK_WRONG_INPUT);
        }
    }

    public static void validateInputNickname(SignupDto.RequestDto requestDto) {

        String nickname = requestDto.getNickname();

        String patternNickname = "^[A-Za-z0-9가-힣]{4,10}$";

        // 닉네임 설정 유효성 검사
        if (nickname == null || !Pattern.matches(patternNickname, nickname)) {
            throw new PrivateException(ErrorCode.SIGNUP_NICKNAME_WRONG_INPUT);
        }
    }

    public static void validateInputMajor(SignupDto.RequestDto requestDto) {

        String major = requestDto.getMajor();

        if (major.isEmpty()) {
            throw new PrivateException(ErrorCode.SIGNUP_MAJOR_WRONG_INPUT);
        }
    }

    public static void validateEmailEmpty(LoginDto loginDto) {

        String email = loginDto.getEmail();

        if (email.isEmpty()) {
            throw new PrivateException(ErrorCode.LOGIN_EMAIL_EMPTY);
        }
    }

    public static void validatePasswordEmpty(LoginDto loginDto) {

        String password = loginDto.getPassword();

        if (password.isEmpty()) {
            throw new PrivateException(ErrorCode.LOGIN_PASSWORD_EMPTY);
        }
    }

    public static void validateRefreshTokenReissue(TokenRequestDto tokenRequestDto) {

        String accessToken = tokenRequestDto.getAccessToken();
        String refreshToken = tokenRequestDto.getRefreshToken();
        Long userId = tokenRequestDto.getUserId();

        if (accessToken == null || refreshToken == null || userId == null) {
            throw new PrivateException(ErrorCode.REFRESH_TOKEN_REISSUE_WRONG_INPUT);
        }
    }
}