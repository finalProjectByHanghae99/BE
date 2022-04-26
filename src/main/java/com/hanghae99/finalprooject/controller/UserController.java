package com.hanghae99.finalprooject.controller;

import com.hanghae99.finalprooject.dto.userDto.LoginDto;
import com.hanghae99.finalprooject.dto.userDto.SignupDto;
import com.hanghae99.finalprooject.dto.userDto.TokenDto;
import com.hanghae99.finalprooject.dto.userDto.TokenRequestDto;
import com.hanghae99.finalprooject.exception.ErrorCode;
import com.hanghae99.finalprooject.exception.ExceptionResponse;
import com.hanghae99.finalprooject.jwt.SecurityUtil;
import com.hanghae99.finalprooject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
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
    public ResponseEntity<Boolean> emailCheck(@RequestBody SignupDto.RequestDto requestDto) {
        return ResponseEntity.ok(userService.emailCheck(requestDto.getEmail()));
    }


    // 닉네임 중복검사 API
    @PostMapping("/user/nicknameCheck")
    public ResponseEntity<ExceptionResponse> nicknameCheck(@RequestBody SignupDto.RequestDto requestDto) {
        userService.nicknameCheck(requestDto.getNickname());
        return new ResponseEntity<>(new ExceptionResponse(ErrorCode.DUPLICATE_ERROR_SIGNUP_NICKNAME), HttpStatus.OK);
    }

    // 로그인 API
    @PostMapping("/user/login")
    public ResponseEntity<ExceptionResponse> login(@RequestBody LoginDto loginDto) {
        TokenDto tokenDto = userService.login(loginDto);
        return  new ResponseEntity<>(new ExceptionResponse(ErrorCode.OK), HttpStatus.OK);
    }

    // 토큰 재발행 API
    @PostMapping("/user/reissue")
    public ResponseEntity<TokenDto> reissue(@RequestBody TokenRequestDto tokenRequestDto) {
        return ResponseEntity.ok(userService.reissue(tokenRequestDto));
    }

    @GetMapping("/test")

    @PostMapping("/post/test")
    public ExceptionResponse testPost(){
        System.out.println(SecurityUtil.getCurrentUserId());
        return new ExceptionResponse(ErrorCode.OK);
    }
}