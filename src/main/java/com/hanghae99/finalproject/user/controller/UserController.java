package com.hanghae99.finalproject.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hanghae99.finalproject.exception.ErrorCode;
import com.hanghae99.finalproject.exception.ExceptionResponse;
import com.hanghae99.finalproject.exception.StatusResponseDto;
import com.hanghae99.finalproject.mail.service.MailService;
import com.hanghae99.finalproject.security.UserDetailsImpl;
import com.hanghae99.finalproject.security.jwt.TokenDto;
import com.hanghae99.finalproject.security.jwt.TokenRequestDto;
import com.hanghae99.finalproject.user.dto.*;
import com.hanghae99.finalproject.user.model.User;
import com.hanghae99.finalproject.user.repository.UserRepository;
import com.hanghae99.finalproject.user.service.KakaoUserService;
import com.hanghae99.finalproject.user.service.UserService;
import com.hanghae99.finalproject.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final KakaoUserService kakaoUserService;
    private final MailService mailService;
    private final UserRepository userRepository;

    // 회원가입 API
    @PostMapping("/user/signup")
    public ResponseEntity<Object> registerUser(@RequestBody SignupRequestDto requestDto) {
        UserInfo userInfo = userService.register(requestDto);
        return new ResponseEntity<>(new StatusResponseDto("회원가입 1차 성공", userInfo), HttpStatus.OK);
    }

    // 아이디 중복검사 API
    @PostMapping("/user/memberIdCheck")
    public ResponseEntity<Object> memberIdCheck(@RequestBody SignupRequestDto requestDto){
        UserValidator.validateInputMemberId(requestDto);
        if(userRepository.existsByMemberId(requestDto.getMemberId())) {
            return new ResponseEntity<>(new ExceptionResponse(ErrorCode.SIGNUP_MEMBERID_DUPLICATE), HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(new StatusResponseDto("사용가능한 아이디입니다", ""), HttpStatus.OK);
        }
    }

    // 닉네임 중복검사 API
    @PostMapping("/user/nicknameCheck")
    public ResponseEntity<Object> nicknameCheck(@RequestBody SignupRequestDto requestDto){
        UserValidator.validateInputNickname(requestDto);
        if(userRepository.existsByNickname(requestDto.getNickname())) {
            return new ResponseEntity<>(new ExceptionResponse(ErrorCode.SIGNUP_NICKNAME_DUPLICATE), HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(new StatusResponseDto("사용가능한 닉네임입니다", ""), HttpStatus.OK);
        }
    }

    // 로그인 API
    @PostMapping("/user/login")
    public ResponseEntity<Object> login(@RequestBody LoginDto loginDto) {
        Map<String, Object> data = userService.login(loginDto);
        return new ResponseEntity<>(new StatusResponseDto("로그인에 성공하셨습니다", data), HttpStatus.OK);
    }

    // 토큰 재발행 API
    @PostMapping("/user/reissue")
    public ResponseEntity<Object> reissue(@RequestBody TokenRequestDto tokenRequestDto) {
        TokenDto tokenDto = userService.reissue(tokenRequestDto);
        return new ResponseEntity<>(new StatusResponseDto("토큰 재발급 성공", tokenDto), HttpStatus.OK);
    }

    // 회원 탈퇴 API
    @DeleteMapping("/user/remove")
    public ResponseEntity<Object> deleteUser(@RequestBody SignOutDto signOutDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        userService.deleteUser(signOutDto, userDetails);
        return new ResponseEntity<>(new StatusResponseDto("회원 탈퇴 성공", ""), HttpStatus.OK);
    }

    // 로그아웃 API
    @PostMapping("/user/logout")
    public ResponseEntity<Object> logout(@RequestBody TokenRequestDto tokenRequestDto) {
        userService.deleteRefreshToken(tokenRequestDto);
        return new ResponseEntity<>(new StatusResponseDto("로그아웃 성공", ""), HttpStatus.OK);
    }

    // 카카오 로그인 API
    @GetMapping("/user/kakao/login")
    public ResponseEntity<Object> kakaoLogin(@RequestParam String code) throws JsonProcessingException {
        KakaoUserInfo kakaoUserInfo = kakaoUserService.kakaoLogin(code);
        return new ResponseEntity<>(userService.SignupUserCheck(kakaoUserInfo.getKakaoId()), HttpStatus.OK);
    }

    // 회원가입 추가 정보 API
    @PostMapping("/user/signup/addInfo")
    public ResponseEntity<Object> addInfo(@RequestBody SignupRequestDto requestDto) {
        TokenDto tokenDto = userService.addInfo(requestDto);
        return new ResponseEntity<>(new StatusResponseDto("추가 정보 등록 성공", tokenDto), HttpStatus.CREATED);
    }
}