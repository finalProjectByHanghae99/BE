package com.hanghae99.finalprooject.controller;

import com.hanghae99.finalprooject.dto.PostDto;
import com.hanghae99.finalprooject.exception.ErrorCode;
import com.hanghae99.finalprooject.exception.ExceptionResponse;
import com.hanghae99.finalprooject.service.AwsS3UploadService;
import com.hanghae99.finalprooject.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
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
                                                       @RequestPart("img") List<MultipartFile> imgList) throws IOException {
       List<String> imgUrlList = awsS3UploadService.uploadImgList(imgList);
       log.info("ImgUrlList : " + imgUrlList);
       postService.createPost(requestDto, imgUrlList);
       return new ResponseEntity<>(new ExceptionResponse(ErrorCode.OK), HttpStatus.OK);
   }
}
