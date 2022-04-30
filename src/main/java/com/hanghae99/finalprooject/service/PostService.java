package com.hanghae99.finalprooject.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.finalprooject.dto.ImgDto;
import com.hanghae99.finalprooject.dto.PostDto;
import com.hanghae99.finalprooject.exception.ErrorCode;
import com.hanghae99.finalprooject.exception.PrivateException;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final ImgRepository imgRepository;
    private final FileUploadService fileUploadService;

    // post 등록
    @Transactional
    public void createPost(String jsonString, List<MultipartFile> imgs, UserDetailsImpl userDetails) throws IOException {
        log.info("multipartFile imgs={}", imgs);
        List<Img> imgList = new ArrayList<>();

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
            Img img = Img.builder()
                    .imgName(imgDto.getImgName())
                    .imgUrl(imgDto.getImgUrl())
                    .build();
            imgList.add(img);
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
}