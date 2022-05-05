package com.hanghae99.finalproject.post.controller;

import com.hanghae99.finalproject.exception.StatusResponseDto;
import com.hanghae99.finalproject.post.dto.PostDto;
import com.hanghae99.finalproject.exception.ErrorCode;
import com.hanghae99.finalproject.exception.ExceptionResponse;
import com.hanghae99.finalproject.security.UserDetailsImpl;
import com.hanghae99.finalproject.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // post 전체 조회 API
    @GetMapping("/api/category")
    public Map<String, List<PostDto.ResponseDto>> home() {
        return postService.home();
    }

    // post 등록 API
    @PostMapping("/api/post")
    public ResponseEntity<ExceptionResponse> createPost(@RequestPart("data") String jsonString,
                                                        @RequestPart(value = "img", required = false) List<MultipartFile> imgs,
                                                        @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        postService.createPost(jsonString, imgs, userDetails);
        return new ResponseEntity<>(new ExceptionResponse(ErrorCode.OK), HttpStatus.OK);
    }

    @GetMapping("/api/post/{postId}")
    public ResponseEntity<Object> getDetailPost(@PathVariable Long postId) {
        PostDto.DetailDto detailDto = postService.getDetail(postId);
        return new ResponseEntity<>(new StatusResponseDto("게시물 상세 조회 성공", detailDto), HttpStatus.OK);
    }

    // post 수정 API
    @PutMapping("/api/post/{postId}")
    public ResponseEntity<ExceptionResponse> editPost(@PathVariable Long postId,
                                                      @RequestPart(value = "data") String jsonString,
                                                      @RequestPart(value = "img", required = false) List<MultipartFile> imgs,
                                                      @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        postService.editPost(postId, jsonString, imgs, userDetails);
        return new ResponseEntity<>(new ExceptionResponse(ErrorCode.OK), HttpStatus.OK);
    }

    // post 삭제 API
    @DeleteMapping("/api/post/{postId}")
    public ResponseEntity<ExceptionResponse> deletePost(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        postService.deletePost(postId, userDetails);
        return new ResponseEntity<>(new ExceptionResponse(ErrorCode.OK), HttpStatus.OK);
    }

    @GetMapping("/api/test/getERRr")
    public void getPosts(){
        throw new RuntimeException();
    }
}