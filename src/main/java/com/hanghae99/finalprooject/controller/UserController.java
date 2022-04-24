package com.hanghae99.finalprooject.controller;

import com.hanghae99.finalprooject.dto.SignupDto;
import com.hanghae99.finalprooject.exception.ErrorCode;
import com.hanghae99.finalprooject.exception.ExceptionResponse;
import com.hanghae99.finalprooject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원가입 API
    @PostMapping("/user/signup")
    public ResponseEntity<ExceptionResponse> registerUser(@RequestBody SignupDto.RequestDto requestDto) {
        userService.registerUser(requestDto);
        return new ResponseEntity<>(new ExceptionResponse(ErrorCode.OK), HttpStatus.OK);
    }

    // 이메일 중복검사 API
    @PostMapping("/user/emailCheck")
    public ResponseEntity<ExceptionResponse> emailCheck(@RequestBody SignupDto.RequestDto requestDto) {
        userService.emailCheck(requestDto.getEmail());
        return new ResponseEntity<>(new ExceptionResponse(ErrorCode.DUPLICATE_ERROR_SIGNUP_EMAIL), HttpStatus.OK);
    }

    // 닉네임 중복검사 API
    @PostMapping("/user/nicknameCheck")
    public ResponseEntity<ExceptionResponse> nicknameCheck(@RequestBody SignupDto.RequestDto requestDto) {
        userService.nicknameCheck(requestDto.getNickname());
        return new ResponseEntity<>(new ExceptionResponse(ErrorCode.DUPLICATE_ERROR_SIGNUP_EMAIL), HttpStatus.OK);
    }
}