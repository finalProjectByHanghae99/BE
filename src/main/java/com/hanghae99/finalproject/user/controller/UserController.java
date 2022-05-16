package com.hanghae99.finalproject.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hanghae99.finalproject.exception.ErrorCode;
import com.hanghae99.finalproject.exception.ExceptionResponse;
import com.hanghae99.finalproject.exception.StatusResponseDto;
import com.hanghae99.finalproject.security.UserDetailsImpl;
import com.hanghae99.finalproject.security.jwt.TokenDto;
import com.hanghae99.finalproject.security.jwt.TokenRequestDto;
import com.hanghae99.finalproject.user.dto.KakaoUserInfo;
import com.hanghae99.finalproject.user.dto.LoginDto;
import com.hanghae99.finalproject.user.dto.SignOutDto;
import com.hanghae99.finalproject.user.dto.SignupDto;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class UserController {
//
    private final UserService userService;
    private final KakaoUserService kakaoUserService;
    private final UserRepository userRepository;

    // 회원가입 API
    @PostMapping("/user/signup")
    public ResponseEntity<ExceptionResponse> registerUser(@RequestBody SignupDto.RequestDto requestDto) {
        userService.registerUser(requestDto);
        return new ResponseEntity<>(new ExceptionResponse(ErrorCode.OK), HttpStatus.OK);
    }
//
    // 이메일 중복검사 API
    @PostMapping("/user/emailCheck")
    public ResponseEntity<ExceptionResponse> emailCheck(@RequestBody SignupDto.RequestDto requestDto){
        UserValidator.validateInputEmail(requestDto);
        if(userRepository.existsByEmail(requestDto.getEmail())) {
            return new ResponseEntity<>(new ExceptionResponse(ErrorCode.SIGNUP_EMAIL_DUPLICATE), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ExceptionResponse(ErrorCode.SIGNUP_EMAIL_CORRECT), HttpStatus.OK);
        }
    }

    // 닉네임 중복검사 API
    @PostMapping("/user/nicknameCheck")
    public ResponseEntity<ExceptionResponse> nicknameCheck(@RequestBody SignupDto.RequestDto requestDto){
        UserValidator.validateInputNickname(requestDto);
        if(userRepository.existsByNickname(requestDto.getNickname())) {
            return new ResponseEntity<>(new ExceptionResponse(ErrorCode.SIGNUP_NICKNAME_DUPLICATE), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ExceptionResponse(ErrorCode.SIGNUP_NICKNAME_CORRECT), HttpStatus.OK);
        }
    }

    // 로그인 API
    @PostMapping("/user/login")
    public ResponseEntity<TokenDto> login(@RequestBody LoginDto loginDto) {
        return ResponseEntity.ok(userService.login(loginDto));
    }

    // 토큰 재발행 API
    @PostMapping("/user/reissue")
    public ResponseEntity<TokenDto> reissue(@RequestBody TokenRequestDto tokenRequestDto) {
        return ResponseEntity.ok(userService.reissue(tokenRequestDto));
    }

    // 회원 탈퇴 API
    @DeleteMapping("/user/remove")
    public ResponseEntity<ExceptionResponse> deleteUser(@RequestBody SignOutDto signOutDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        userService.deleteUser(signOutDto, userDetails);
        return new ResponseEntity<>(new ExceptionResponse(ErrorCode.OK), HttpStatus.OK);
    }

    // 로그아웃 API
    @PostMapping("/user/logout")
    public ResponseEntity<ExceptionResponse> logout(@RequestBody TokenRequestDto tokenRequestDto) {
        userService.deleteRefreshToken(tokenRequestDto);
        return new ResponseEntity<>(new ExceptionResponse(ErrorCode.OK), HttpStatus.OK);
    }

    // 카카오 로그인 API
    @GetMapping("/user/kakao/callback")
    public ResponseEntity<Object> kakaoLogin(@RequestParam String code) throws JsonProcessingException {
        KakaoUserInfo kakaoUserInfo = kakaoUserService.kakaoLogin(code);

        return new ResponseEntity<>(userService.SignupUserCheck(kakaoUserInfo.getKakaoId()), HttpStatus.OK);
    }

    // 회원가입 추가 정보 API
    @PostMapping("/user/signup/addInfo")
    public ResponseEntity<Object> addInfo(@RequestBody SignupDto.RequestDto requestDto) {
        TokenDto tokenDto = userService.addInfo(requestDto);
//        return new ResponseEntity<>(new StatusResponseDto("추가 정보가 입력되었습니다", data), HttpStatus.OK);
        return new ResponseEntity<>(new StatusResponseDto("추가 정보 등록 성공", tokenDto), HttpStatus.CREATED);
    }


}