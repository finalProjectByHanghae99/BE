package com.hanghae99.finalproject.controller;

import com.hanghae99.finalproject.exception.ErrorCode;
import com.hanghae99.finalproject.exception.ExceptionResponse;
import com.hanghae99.finalproject.security.UserDetailsImpl;
import com.hanghae99.finalproject.service.UserApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserApplyController {

    private final UserApplyService userApplyService;

    // 협업 신청 API
    @PostMapping("/api/recruit/{postId}/apply")
    public ResponseEntity<ExceptionResponse> apply(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        userApplyService.apply(postId, userDetails);
        return new ResponseEntity<>(new ExceptionResponse(ErrorCode.OK), HttpStatus.OK);
    }
}