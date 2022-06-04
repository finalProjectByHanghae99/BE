package com.hanghae99.finalproject.common.validator;

import com.hanghae99.finalproject.security.jwt.TokenRequestDto;
import com.hanghae99.finalproject.user.dto.LoginDto;
import com.hanghae99.finalproject.common.exception.ErrorCode;
import com.hanghae99.finalproject.common.exception.CustomException;
import com.hanghae99.finalproject.user.dto.SignupRequestDto;

import java.util.regex.Pattern;

public class UserValidator {
    public static void validateInputMemberId(SignupRequestDto requestDto) {

        String memberId = requestDto.getMemberId();

        String patternMemberId = "^[a-zA-Z0-9]{4,12}$";

        // 이메일 설정 유효성 검사
        if (memberId == null || !Pattern.matches(patternMemberId, memberId)) {
            throw new CustomException(ErrorCode.SIGNUP_MEMBERID_WRONG_INPUT);
        }
    }

    public static void validateInputPassword(SignupRequestDto requestDto) {

        String password = requestDto.getPassword();
        String pwCheck = requestDto.getPwCheck();

        String patternPw = "^[A-Za-z0-9]{6,20}$";

        // 비밀번호 설정 유효성 검사
        if (password == null || !Pattern.matches(patternPw, password)) {
            throw new CustomException(ErrorCode.SIGNUP_PASSWORD_WRONG_INPUT);
        }

        // 비밀번호 확인 유효성 검사
        if (!password.equals(pwCheck)) {
            throw new CustomException(ErrorCode.SIGNUP_PWCHECK_WRONG_INPUT);
        }
    }

    public static void validateInputNickname(SignupRequestDto requestDto) {

        String nickname = requestDto.getNickname();

        String patternNickname = "^[A-Za-z0-9가-힣]{2,6}$";

        // 닉네임 설정 유효성 검사
        if (nickname == null || !Pattern.matches(patternNickname, nickname)) {
            throw new CustomException(ErrorCode.SIGNUP_NICKNAME_WRONG_INPUT);
        }
    }

    public static void validateInputMajor(SignupRequestDto requestDto) {

        String major = requestDto.getMajor();

        if (major.isEmpty()) {
            throw new CustomException(ErrorCode.SIGNUP_MAJOR_WRONG_INPUT);
        }
    }

    public static void validateMemberIdEmpty(LoginDto loginDto) {

        String memberId = loginDto.getMemberId();

        if (memberId.isEmpty()) {
            throw new CustomException(ErrorCode.LOGIN_MEMBERID_EMPTY);
        }
    }

    public static void validatePasswordEmpty(LoginDto loginDto) {

        String password = loginDto.getPassword();

        if (password.isEmpty()) {
            throw new CustomException(ErrorCode.LOGIN_PASSWORD_EMPTY);
        }
    }

    public static void validateRefreshTokenReissue(TokenRequestDto tokenRequestDto) {

        String accessToken = tokenRequestDto.getAccessToken();
        String refreshToken = tokenRequestDto.getRefreshToken();
        Long userId = tokenRequestDto.getUserId();

        if (accessToken == null || refreshToken == null || userId == null) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_REISSUE_WRONG_INPUT);
        }
    }
}