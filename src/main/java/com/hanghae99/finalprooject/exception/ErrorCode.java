package com.hanghae99.finalprooject.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    OK(HttpStatus.OK,  "200", "성공"),

    WRONG_INPUT_SIGNUP_EMAIL(HttpStatus.BAD_REQUEST, "100", "Email 형식을 맞춰주세요"),
    WRONG_INPUT_SIGNUP_PASSWORD(HttpStatus.BAD_REQUEST, "101", "비밀번호 형식을 맞춰주세요"),
    WRONG_INPUT_SIGNUP_PWCHECK(HttpStatus.BAD_REQUEST, "102", "비밀번호가 일치하지 않습니다"),
    WRONG_INPUT_SIGNUP_NICKNAME(HttpStatus.BAD_REQUEST, "103", "닉네임 형식을 맞춰주세요"),
    DUPLICATE_ERROR_SIGNUP_EMAIL(HttpStatus.BAD_REQUEST, "104", "중복된 Email입니다"),
    DUPLICATE_ERROR_SIGNUP_NICKNAME(HttpStatus.BAD_REQUEST, "105", "중복된 닉네임입니다");

    private final HttpStatus status;
    private final String errorCode;
    private final String errorMessage;
}