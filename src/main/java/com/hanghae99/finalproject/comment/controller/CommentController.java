package com.hanghae99.finalproject.comment.controller;

import com.hanghae99.finalproject.comment.dto.CommentDto;
import com.hanghae99.finalproject.exception.ErrorCode;
import com.hanghae99.finalproject.exception.ExceptionResponse;
import com.hanghae99.finalproject.exception.StatusResponseDto;
import com.hanghae99.finalproject.security.UserDetailsImpl;
import com.hanghae99.finalproject.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 등록 API
    @PostMapping("/api/comment")
//    public ResponseEntity<Object> createComment(@RequestBody CommentDto.RequestDto requestDto,
//                                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        CommentDto.CreateResponseDto responseDto = commentService.createComment(requestDto, userDetails);
//        return new ResponseEntity<>(new StatusResponseDto("댓글 등록 성공", responseDto), HttpStatus.OK);
//    }
    public ResponseEntity<ExceptionResponse> createComment(@RequestBody CommentDto.RequestDto requestDto,
                                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        commentService.createComment(requestDto, userDetails);
        return new ResponseEntity<>(new ExceptionResponse(ErrorCode.OK), HttpStatus.OK);
    }

    // 댓글 수정 API
    @PutMapping("/api/comment/{commentId}")
    public ResponseEntity<ExceptionResponse> editComment(@PathVariable Long commentId,
                                                         @RequestBody CommentDto.RequestDto requestDto,
                                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        commentService.editComment(commentId, requestDto, userDetails);
        return new ResponseEntity<>(new ExceptionResponse(ErrorCode.OK), HttpStatus.OK);
    }

    // 댓글 삭제 API
    @DeleteMapping("/api/comment/{commentId}")
    public ResponseEntity<ExceptionResponse> deletePost(@PathVariable Long commentId,
                                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        commentService.deleteComment(commentId, userDetails);
        return new ResponseEntity<>(new ExceptionResponse(ErrorCode.OK), HttpStatus.OK);
    }
}