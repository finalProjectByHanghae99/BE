package com.hanghae99.finalprooject.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.finalprooject.dto.ImgDto;
import com.hanghae99.finalprooject.dto.PostDto;
import com.hanghae99.finalprooject.exception.ErrorCode;
import com.hanghae99.finalprooject.exception.PrivateException;
import com.hanghae99.finalprooject.model.CurrentStatus;
import com.hanghae99.finalprooject.model.Img;
import com.hanghae99.finalprooject.model.Post;
import com.hanghae99.finalprooject.model.User;
import com.hanghae99.finalprooject.repository.ImgRepository;
import com.hanghae99.finalprooject.repository.PostRepository;
import com.hanghae99.finalprooject.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final AwsS3UploadService awsS3UploadService;

    // post 등록
//    @Transactional
//    public void createPost(PostDto.RequestDto requestDto, List<String> imgUrlList, UserDetailsImpl userDetails) {
//
//        // 일단 post 작성이 이미지 필수 등록으로 설정....(회의해봐야함)
//        postBlankCheck(imgUrlList);
//
//        User user = userDetails.getUser();
//
//        String title = requestDto.getTitle();
//        String content = requestDto.getContent();
//        String deadline = requestDto.getDeadline();
//        CurrentStatus currentStatus = requestDto.getCurrentStatus();
//        String region = requestDto.getRegion();
//        String category = requestDto.getCategory();
//
//        Post post = new Post(title, content, deadline, currentStatus, region, category, user);
//
//        List<String> imgList = new ArrayList<>();
//        for (String imgUrl : imgUrlList) {
//            Img img = new Img(imgUrl, post);
//            imgRepository.save(img);
//            imgList.add(img.getImgUrl());
//        }
//        postRepository.save(post);
//    }
//
//    private void postBlankCheck(List<String> imgPaths) {
//        if(imgPaths == null || imgPaths.isEmpty()){
//            throw new PrivateException(ErrorCode.WRONG_INPUT_IMAGE);
//        }
//    }

    // post 등록 2차 방안
    @Transactional
    public void createPost(String jsonString, List<MultipartFile> imgs, UserDetailsImpl userDetails) throws IOException {
        List<Img> imgList = new ArrayList<>();

        List<ImgDto> imgDtoList = new ArrayList<>();

        if (imgs != null) {
            for (MultipartFile img : imgs) {
                ImgDto imgDto = awsS3UploadService.uploadImage(img);
                imgDtoList.add(imgDto);
            }
        }

        ObjectMapper objectMapper = new ObjectMapper();
        PostDto.RequestDto requestDto = objectMapper.readValue(jsonString, PostDto.RequestDto.class);

        User user = userDetails.getUser();
        dtoParser(imgList, imgDtoList);
        Post post = Post.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .deadline(requestDto.getDeadline())
                .currentStatus(requestDto.getCurrentStatus())
                .region(requestDto.getRegion())
                .category(requestDto.getCategory())
                .imgList(imgList)
                .user(user)
                .build();

        postRepository.save(post);
    }

    private void dtoParser(List<Img> imgList, List<ImgDto> imgDtoList) {
        for (ImgDto imgDto : imgDtoList) {
            Img image = Img.builder()
                    .imgUrl(imgDto.getImgUrl())
                    .build();
            imgList.add(image);
        }
    }
}