package com.hanghae99.finalprooject.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    OK(HttpStatus.OK,  "200", "true"),

    // 회원가입
    WRONG_INPUT_SIGNUP_EMAIL(HttpStatus.BAD_REQUEST, "400", "Email 형식을 맞춰주세요"),
    WRONG_INPUT_SIGNUP_PASSWORD(HttpStatus.BAD_REQUEST, "400", "비밀번호 형식을 맞춰주세요"),
    WRONG_INPUT_SIGNUP_PWCHECK(HttpStatus.BAD_REQUEST, "400", "비밀번호가 일치하지 않습니다"),
    WRONG_INPUT_SIGNUP_NICKNAME(HttpStatus.BAD_REQUEST, "400", "닉네임 형식을 맞춰주세요"),
    DUPLICATE_CHECK_SIGNUP_EMAIL(HttpStatus.BAD_REQUEST, "400", "Email 중복확인을 해주세요"),
    DUPLICATE_CHECK_SIGNUP_NICKNAME(HttpStatus.BAD_REQUEST, "400", "닉네임 중복확인을 해주세요"),

    // Token
    JWT_TOKEN_WRONG_AUTHORITY(HttpStatus.FORBIDDEN, "403", "권한 정보가 없는 토큰입니다"),
    JWT_TOKEN_WRONG_SIGNATURE(HttpStatus.UNAUTHORIZED, "401", "잘못된 JWT 서명입니다"),
    JWT_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "401", "만료된 JWT 토큰입니다."),
    JWT_TOKEN_NOT_SUPPORTED(HttpStatus.UNAUTHORIZED, "401", "지원되지 않는 JWT 토큰입니다."),
    JWT_TOKEN_WRONG_FORM(HttpStatus.UNAUTHORIZED, "401", "JWT 토큰이 잘못되었습니다."),
    JWT_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "유효한 JWT 토큰이 없습니다"),

    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "401", "Refresh Token이 만료되었습니다"),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "Refresh Token이 존재하지 않습니다"),
    REFRESH_TOKEN_NOT_MATCH(HttpStatus.UNAUTHORIZED, "401", "Refresh Token이 일치하지 않습니다"),

    // 로그인
    LOGIN_NOT_FOUNT_EMAIL(HttpStatus.NOT_FOUND, "404", "해당 Email을 찾을 수 없습니다"),

    //기타
    NOT_FOUND_AUTHORIZATION_IN_SECURITY_CONTEXT(HttpStatus.INTERNAL_SERVER_ERROR, "998", "Security Context에 인증 정보가 없습니다.");

    private final HttpStatus status;
    private final String errorCode;
    private final String errorMessage;
}