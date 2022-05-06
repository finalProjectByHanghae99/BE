package com.hanghae99.finalproject.user.controller;

import com.hanghae99.finalproject.exception.ErrorCode;
import com.hanghae99.finalproject.exception.ExceptionResponse;
import com.hanghae99.finalproject.exception.StatusResponseDto;
import com.hanghae99.finalproject.security.UserDetailsImpl;
import com.hanghae99.finalproject.user.dto.UserApplyRequestDto;
import com.hanghae99.finalproject.user.service.UserApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserApplyController {

    private final UserApplyService userApplyService;

    // 모집 지원 API
    @PostMapping("/api/apply/{postId}")
    public ResponseEntity<Object> apply(@PathVariable Long postId, @RequestBody UserApplyRequestDto userApplyRequestDto,
                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        userApplyService.apply(postId, userApplyRequestDto, userDetails);
        return new ResponseEntity<>(new StatusResponseDto("신청 성공", ""), HttpStatus.OK);
    }

    // 모집 지원 취소 API
    @DeleteMapping("/api/apply/{postId}")
    public ResponseEntity<Object> cancelApply(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        userApplyService.cancelApply(postId, userDetails);
        return new ResponseEntity<>(new StatusResponseDto("신청 취소 성공", ""), HttpStatus.OK);
    }

    // 모집 마감 API
    @PatchMapping("/api/apply/{postId}/over")
    public ResponseEntity<Object> overApply(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        userApplyService.overApply(postId, userDetails);
        return new ResponseEntity<>(new StatusResponseDto("모집 마감 성공", ""), HttpStatus.OK);
    }
}