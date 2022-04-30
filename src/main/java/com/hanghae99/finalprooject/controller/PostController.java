package com.hanghae99.finalprooject.controller;

import com.hanghae99.finalprooject.dto.PostDto;
import com.hanghae99.finalprooject.exception.ErrorCode;
import com.hanghae99.finalprooject.exception.ExceptionResponse;
import com.hanghae99.finalprooject.security.UserDetailsImpl;
import com.hanghae99.finalprooject.service.AwsS3UploadService;
import com.hanghae99.finalprooject.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final AwsS3UploadService awsS3UploadService;

    // post 등록 API
    @PostMapping("/api/post")
    public ResponseEntity<ExceptionResponse> createPost(@RequestPart("data") PostDto.RequestDto requestDto,
                                                        @RequestPart("img") List<MultipartFile> imgList,
                                                        @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        List<String> imgUrlList = awsS3UploadService.uploadImg(imgList);
        log.info("ImgUrlList : " + imgUrlList);
        postService.createPost(requestDto, imgUrlList, userDetails);
        return new ResponseEntity<>(new ExceptionResponse(ErrorCode.OK), HttpStatus.OK);
    }

    // post 상세 조회 API
    @GetMapping("/api/post/{postId}")
    public PostDto.DetailDto getDetailPost(@PathVariable Long postId) {
        return postService.getDetail(postId);
    }

    // post 수정 API
    @PutMapping("/api/post/{postId}")
    public ResponseEntity<ExceptionResponse> updatePost(@PathVariable Long postId,
                                                        @RequestPart("data") PostDto.RequestDto requestDto,
                                                        @RequestPart(value = "img",required = false) List<MultipartFile> imgList,
                                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        List<String> imgUrlList = awsS3UploadService.uploadImg(imgList);
//        log.info("ImgUrlList : " + imgUrlList);


        postService.updatePost(postId, requestDto, imgList, userDetails);
        return new ResponseEntity<>(new ExceptionResponse(ErrorCode.OK), HttpStatus.OK);
    }

    // post 삭제 API
    @DeleteMapping("/api/post/{postId}")
    public ResponseEntity<ExceptionResponse> deletePost(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        postService.deletePost(postId, userDetails);
        return new ResponseEntity<>(new ExceptionResponse(ErrorCode.OK), HttpStatus.OK);
    }
}