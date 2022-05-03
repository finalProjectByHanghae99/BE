package com.hanghae99.finalproject.controller;

import com.hanghae99.finalproject.dto.userDto.MyPageDto;
import com.hanghae99.finalproject.exception.ErrorCode;
import com.hanghae99.finalproject.exception.ExceptionResponse;
import com.hanghae99.finalproject.security.UserDetailsImpl;
import com.hanghae99.finalproject.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@RequiredArgsConstructor
@RestController
public class MyPageController {


    private final MyPageService myPageService;


    //유저 정보 조회 및 수정
    //자신 or 타인의 유저 정보를 볼 수 있다 .
    @GetMapping("/user/info/{userId}")
    public MyPageDto.ResponseDto userInfo(@PathVariable Long userId){
        return myPageService.findUserPage(userId);
    }


    @PutMapping("/user/info/{userId}/modify")
    public ResponseEntity<ExceptionResponse> userInfoModify(@PathVariable Long userId,
                                                            @RequestPart MyPageDto.RequestDto requestDto,
                                                            @RequestPart List<MultipartFile> imgs,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {

        // userId를 받아와 수정하는 유저 정보를 반환받는다.
        // reqeustDto에는 수정해야할 내용들을 받아온다. + 변경된 urlPath <Hidden>도 존재한다.
        // 새롭게 저장할 파일들을 가져온다.
        myPageService.modifyUserInfo(userId,requestDto,imgs,userDetails);
        return new ResponseEntity<>(new ExceptionResponse(ErrorCode.OK), HttpStatus.OK);
    }

    //신청 / 모집 / 모집 완료 조회
    //신청한 모집글은 본인만 확인할 수 있다.
    // 유저 pk를 이용하여 해당 유저가 '신청' 한 게시글 목록을 가져와야한다.
    @GetMapping("/user/applied")
    public List<MyPageDto.AppliedResponseDto> userInfoApplied(@AuthenticationPrincipal UserDetailsImpl userDetails){

        return myPageService.responseAppliedList(userDetails);

    }




}
