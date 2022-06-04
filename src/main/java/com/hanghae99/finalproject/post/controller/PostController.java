package com.hanghae99.finalproject.post.controller;

import com.hanghae99.finalproject.common.exception.StatusResponseDto;
import com.hanghae99.finalproject.post.dto.PostDto;
import com.hanghae99.finalproject.post.service.PostService;
import com.hanghae99.finalproject.security.UserDetailsImpl;
import io.swagger.annotations.ApiOperation;
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


    @ApiOperation(value = "모집글 등록",notes = "모집글을 등록한다.")
    @PostMapping("/api/post")
    public ResponseEntity<Object> createPost(@RequestPart("data") String jsonString,
                                                        @RequestPart(value = "img", required = false) List<MultipartFile> imgs,
                                                        @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        postService.createPost(jsonString, imgs, userDetails);
        return new ResponseEntity<>(new StatusResponseDto("게시물 등록 성공", ""), HttpStatus.OK);
    }


    @ApiOperation(value = "모집글 상세 조회",notes = "하나의 모집글을 조회한다.")
    @GetMapping("/api/post/{postId}")
    public ResponseEntity<Object> getDetailPost(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        PostDto.DetailDto detailDto = postService.getDetail(postId, userDetails);
        return new ResponseEntity<>(new StatusResponseDto("게시물 상세 조회 성공", detailDto), HttpStatus.OK);
    }

    // post 수정 API

    @PutMapping("/api/post/{postId}")
    public ResponseEntity<Object> editPost(@PathVariable Long postId,
                                                      @RequestPart(value = "data") String jsonString,
                                                      @RequestPart(value = "img", required = false) List<MultipartFile> imgs,
                                                      @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        postService.editPost(postId, jsonString, imgs, userDetails);
        return new ResponseEntity<>(new StatusResponseDto("게시물 수정 성공", ""), HttpStatus.OK);
    }

    // post 삭제 API
    @DeleteMapping("/api/post/{postId}")
    public ResponseEntity<Object> deletePost(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        postService.deletePost(postId, userDetails);
        return new ResponseEntity<>(new StatusResponseDto("게시물 삭제 성공", ""), HttpStatus.OK);
    }
}