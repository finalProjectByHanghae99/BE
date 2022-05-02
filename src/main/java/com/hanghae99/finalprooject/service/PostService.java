package com.hanghae99.finalprooject.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.finalprooject.dto.ImgDto;
import com.hanghae99.finalprooject.dto.ImgUrlDto;
import com.hanghae99.finalprooject.dto.MajorDto;
import com.hanghae99.finalprooject.dto.PostDto;
import com.hanghae99.finalprooject.exception.ErrorCode;
import com.hanghae99.finalprooject.exception.PrivateException;
import com.hanghae99.finalprooject.model.*;
import com.hanghae99.finalprooject.repository.ImgRepository;
import com.hanghae99.finalprooject.repository.PostRepository;
import com.hanghae99.finalprooject.repository.UserRepository;
import com.hanghae99.finalprooject.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final ImgRepository imgRepository;
    private final UserRepository userRepository;

    private final FileUploadService fileUploadService;
    private final AwsS3UploadService s3UploadService;

    // post 등록
    @Transactional
    public void createPost(String jsonString, List<MultipartFile> imgs, UserDetailsImpl userDetails) throws IOException {
        log.info("multipartFile imgs={}", imgs);
        List<Img> imgList = new ArrayList<>();
        List<Major> majorList = new ArrayList<>();

        List<ImgDto> imgDtoList = new ArrayList<>();

        if (imgs != null) {
            for (MultipartFile img : imgs) {
                ImgDto imgDto = fileUploadService.uploadImage(img);
                imgDtoList.add(imgDto);
            }
        }

        // 자료형이 String인 jsonString을 Dto로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        PostDto.RequestDto requestDto = objectMapper.readValue(jsonString, PostDto.RequestDto.class);

        User user = userDetails.getUser();
        dtoParser(imgList, imgDtoList, majorList, requestDto);
        Post post = Post.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .deadline(requestDto.getDeadline())
                .currentStatus(CurrentStatus.ONGOING)
                .region(requestDto.getRegion())
                .link(requestDto.getLink())
                .imgList(imgList)
                .majorList(majorList)
                .user(user)
                .build();
        postRepository.save(post);
    }

    private void dtoParser(List<Img> imgList, List<ImgDto> imgDtoList, List<Major> majorList, PostDto.RequestDto requestDto) {
        for (ImgDto imgDto : imgDtoList) {
            Img img = Img.builder()
                    .imgName(imgDto.getImgName())
                    .imgUrl(imgDto.getImgUrl())
                    .build();
            imgList.add(img);
        }

        for (MajorDto.RequestDto majorRequestDto : requestDto.getMajorList()) {
            Major major = Major.builder()
                    .majorName(majorRequestDto.getMajorName())
                    .numOfPeopleSet(majorRequestDto.getNumOfPeopleSet())
                    .numOfPeopleApply(0)
                    .build();
            majorList.add(major);
        }
    }

    // post 상세 조회
    public PostDto.DetailDto getDetail(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new PrivateException(ErrorCode.POST_NOT_FOUND)
        );

        List<String> imgUrl = imgRepository.findAllByPost(post)
                .stream()
                .map(Img::getImgUrl)
                .collect(Collectors.toList());

        // 댓글 추후 추가
        return new PostDto.DetailDto(postId, post, imgUrl);
    }

    // post 수정
    @Transactional
    public void editPost(Long postId, String jsonString, List<MultipartFile> imgs, UserDetailsImpl userDetails) throws IOException {
        log.info("수정 내용={}", jsonString);

        // 자료형이 String인 jsonString을 Dto로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        PostDto.PutRequestDto putRequestDto = objectMapper.readValue(jsonString, PostDto.PutRequestDto.class);

        Post post = postRepository.findById(postId).orElseThrow(
                () -> new PrivateException(ErrorCode.POST_NOT_FOUND)
        );

        User user = userRepository.findByNickname(userDetails.getUser().getNickname()).orElseThrow(
                () -> new PrivateException(ErrorCode.NOT_FOUND_USER_INFO)
        );

        // 본인 post만 수정 가능
        if (!post.getUser().equals(user)) {
            throw new PrivateException(ErrorCode.POST_UPDATE_WRONG_ACCESS);
        }

        List<Img> imgList = post.getImgList();
        List<ImgDto> imgDtoList = new ArrayList<>();
        List<Img> removeImgList = new ArrayList<>();
        // 수정할 이미지 S3, 이미지 DB에서 삭제하기
        for (Img img : imgList) {
            for (ImgUrlDto imgUrlDto : putRequestDto.getImgUrl()) {
                if (img.getImgUrl().equals(imgUrlDto.getImgUrl())) {
                    s3UploadService.deleteFile(img.getImgName());
                    imgRepository.deleteById(img.getId());
                    // removeImgList에 수정할 이미지 담기
                    removeImgList.add(img);
                }
            }
        }

        // / removeImgList에 담긴 수정 이미지 원래 Imglist에서 제거
        for (Img img : removeImgList) {
            imgList.remove(img);
        }

        // 추가할 이미지 S3에 저장
        if (imgs != null) {
            for (MultipartFile img : imgs) {
                log.info("이미지 존재 유무={}", img.isEmpty());
                if(!img.isEmpty()) {
                    ImgDto imgDto = fileUploadService.uploadImage(img);
                    imgDtoList.add(imgDto);
                }
            }
        }

        putDtoParser(imgList, imgDtoList);
        post.updatePost(putRequestDto, imgList);
    }

    private void putDtoParser(List<Img> imgList, List<ImgDto> imgDtoList) {
        for (ImgDto imgDto : imgDtoList) {
            Img img = Img.builder()
                    .imgName(imgDto.getImgName())
                    .imgUrl(imgDto.getImgUrl())
                    .build();
            imgList.add(img);
        }
    }

    // post 삭제
    @Transactional
    public void deletePost(Long postId, UserDetailsImpl userDetails) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new PrivateException(ErrorCode.POST_NOT_FOUND)
        );

        User user = userRepository.findByNickname(userDetails.getUser().getNickname()).orElseThrow(
                () -> new PrivateException(ErrorCode.NOT_FOUND_USER_INFO)
        );

        // 본인 post만 삭제 가능
        if (!post.getUser().equals(user)) {
            throw new PrivateException(ErrorCode.POST_DELETE_WRONG_ACCESS);
        }

        postRepository.deleteById(postId);
    }
}