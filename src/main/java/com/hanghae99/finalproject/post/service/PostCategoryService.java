package com.hanghae99.finalproject.post.service;

import com.hanghae99.finalproject.img.ImgResponseDto;
import com.hanghae99.finalproject.post.dto.PostCategoryRequestDto;
import com.hanghae99.finalproject.post.dto.PostCategoryResponseDto;
import com.hanghae99.finalproject.post.repository.PostRepository;
import com.hanghae99.finalproject.user.dto.MajorDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostCategoryService {

    private final PostRepository postRepository;

    @Transactional
    public Map<String, Object> home(PostCategoryRequestDto postCategoryRequestDto, Pageable pageable) {

        // 페이징 및 카테고리별 필터 처리된 post들을 pagePost에 담기
        Page<PostCategoryResponseDto> pagePost = postRepository.filterPagePost(postCategoryRequestDto, pageable);

        Map<String, Object> data = new HashMap<>();

        // 카테고리별 필터 처리된 후 조회된 postId 값 모음
        List<Long> postIdCollect = pagePost.stream().map(PostCategoryResponseDto::getPostId).collect(Collectors.toList());


        // postId별 imgUrl 리스트에 담기
        List<ImgResponseDto> imgUrlList = postRepository.imgFilter(postIdCollect);
        Map<Long, List<ImgResponseDto>> imgIdMap = imgUrlList
                .stream().collect(Collectors.groupingBy(ImgResponseDto::getPostId));

//        String imgUrl;
//        if (imgUrlList.isEmpty()) {
//            imgUrl = "https://hyemco-butket.s3.ap-northeast-2.amazonaws.com/postDefaultImg.PNG";
//        } else {
//            imgUrl = String.valueOf(imgIdMap.get(0L));
//        }

        // postId별 majorName 리스트에 담기
        List<MajorDto.ResponseDto> majorList = postRepository.majorFilter(postIdCollect);
        Map<Long, List<MajorDto.ResponseDto>> majorIdMap = majorList
                .stream().collect(Collectors.groupingBy(MajorDto.ResponseDto::getPostId));

        // PostCategoryResponseDto에 imgUrl, major 컬렉션 묶어주기
        for (PostCategoryResponseDto key : pagePost) {
            key.setImgUrl(Optional.ofNullable(imgIdMap.get(key.getPostId())).orElse(new ArrayList<>()));
        }
        for (PostCategoryResponseDto key : pagePost) {
            key.setMajorList(Optional.ofNullable(majorIdMap.get(key.getPostId())).orElse(new ArrayList<>()));
        }
        data.put("data", pagePost.getContent());

        return data;
    }
}