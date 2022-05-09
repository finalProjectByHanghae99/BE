package com.hanghae99.finalproject.user.controller;

import com.hanghae99.finalproject.user.dto.AcceptedDto;
import com.hanghae99.finalproject.user.dto.MyPageDto;
import com.hanghae99.finalproject.exception.ErrorCode;
import com.hanghae99.finalproject.exception.ExceptionResponse;
import com.hanghae99.finalproject.security.UserDetailsImpl;
import com.hanghae99.finalproject.user.dto.RejectDto;
import com.hanghae99.finalproject.user.service.MyPageService;
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


    @PatchMapping("/user/info/{userId}/modify")
    public ResponseEntity<ExceptionResponse> userInfoModify(@PathVariable Long userId,
                                                            @RequestPart MyPageDto.RequestDto requestDto,
                                                            @RequestPart(required = false) List<MultipartFile> imgs,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {

        // userId를 받아와 수정하는 유저 정보를 반환받는다.
        // reqeustDto에는 수정해야할 내용들을 받아온다. + 변경된 urlPath <Hidden>도 존재한다.
        // 새롭게 저장할 파일들을 가져온다.
        myPageService.modifyUserInfo(userId,requestDto,imgs,userDetails);
        return new ResponseEntity<>(new ExceptionResponse(ErrorCode.OK), HttpStatus.OK);
    }

    //신청 / 모집 / 모집 완료 조회
    //신청한 모집글은 본인만 확인할 수 있다.
    //유저 pk를 이용하여 해당 유저가 '신청' 한 게시글 목록을 가져와야한다.
    @GetMapping("/user/applied")
    public List<MyPageDto.AppliedResponseDto> userInfoApplied(@AuthenticationPrincipal UserDetailsImpl userDetails){

        return myPageService.responseAppliedList(userDetails);

    }

    //유저가 작성한 모집글 리스트들을 반환한다.
    @GetMapping("/user/recruiting/{userId}")
    public List<MyPageDto.RecruitResponseDto> userInfoRecruit(@PathVariable Long userId){

        return myPageService.responsePostRecruitList(userId);

    }
    //유저가 작성한 모집글 리스트 에서 본 유저의 '모집글'에서 '신청하기'를 한 '신청자' 리스트들을 반환한다.
    @GetMapping("/user/apply/{postId}")
    public MyPageDto.ResponseEntityToPost userInfoApplyMyPost(@PathVariable Long postId,@RequestParam(value = "isAccepted") int isAccepted ){

        return myPageService.responseApplyMyPostUserList(postId,isAccepted);
    }

    //유저가 수락 시 작성한 모집글의 신청자의 Accepted 상태를 = 1 로 변경해준다.

    @PostMapping("/user/apply/accepted")
    public void AcceptedApply(@RequestBody AcceptedDto acceptedDto){

        myPageService.modifyAcceptedStatus(acceptedDto);

    }

    // 신청자/ 팀원 목록에서 거절 or 퇴장
    @PostMapping("/user/apply/reject")
    public void rejectApply(@RequestBody RejectDto rejectDto){

        myPageService.rejectUserApply(rejectDto);
    }

    //모집마감 목록
    //해당 유저 ID를 가진 post글들을 전부가져온다.
    @GetMapping("/user/over/{userId}")
    public List<MyPageDto.RecruitOverList> userInfoRecruitOverList(@PathVariable Long userId){

        return myPageService.findRecruitOverList(userId);
    }

    // 모집 마감 -> 팀원 리뷰 -> 모집글 pk 를 받아와 해당 모집글의 참여자들 명단을 반환해준다.
    @GetMapping("/user/recruiting/evaluation/{postId}")
    public List<MyPageDto.RecruitUserList> userInfoRecruitUserList(@PathVariable Long postId){

        return myPageService.findRecruitUserList(postId);
    }

    // 참여자 유저 리스트에서 특정 유저에게 평점을 내려준다.
    @PostMapping("/user/recruiting/evaluation")
    public ResponseEntity<ExceptionResponse> userInfoRecruitUserEvaluation(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody MyPageDto.RequestUserRate requestUserRate) {

        myPageService.EvaluationUser(requestUserRate,userDetails);

        return new ResponseEntity<>(new ExceptionResponse(ErrorCode.OK), HttpStatus.OK);
    }
}
