package com.hanghae99.finalprooject.controller;

import com.hanghae99.finalprooject.dto.PostDto;
import com.hanghae99.finalprooject.exception.ErrorCode;
import com.hanghae99.finalprooject.exception.ExceptionResponse;
import com.hanghae99.finalprooject.security.UserDetailsImpl;
import com.hanghae99.finalprooject.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // post 등록 API
    @PostMapping("/api/post")
    public ResponseEntity<ExceptionResponse> createPost(@RequestPart("data") String jsonString,
                                                        @RequestPart(value = "img", required = false) List<MultipartFile> imgs,
                                                        @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        postService.createPost(jsonString, imgs, userDetails);
        return new ResponseEntity<>(new ExceptionResponse(ErrorCode.OK), HttpStatus.OK);
    }

    // post 상세 조회 API
    @GetMapping("/api/post/{postId}")
    public PostDto.DetailDto getDetailPost(@PathVariable Long postId) {
        return postService.getDetail(postId);
    }
}