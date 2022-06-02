package com.hanghae99.finalproject.post.service;

import com.hanghae99.finalproject.img.dto.ImgResponseDto;
import com.hanghae99.finalproject.post.dto.PostAllResponseDto;
import com.hanghae99.finalproject.post.dto.PostFilterRequestDto;
import com.hanghae99.finalproject.post.dto.PostFilterResponseDto;
import com.hanghae99.finalproject.post.model.Post;
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
public class PostFilterService {

    private final PostRepository postRepository;

    // 랜딩 페이지 POST CARD 조회
    @Transactional
    public Map<String, Object> landingPage() {

        List<PostFilterResponseDto> landingPagePost = postRepository.filterLandingPage();

        Map<String, Object> data = new HashMap<>();

        // 카테고리별 필터 처리된 후 조회된 postId 값 모음
        List<Long> postIdCollect = landingPagePost.stream().map(PostFilterResponseDto::getPostId).collect(Collectors.toList());

        // postId별 imgUrl 리스트에 담기
        List<ImgResponseDto> imgUrlList = postRepository.imgFilter(postIdCollect);
        Map<Long, List<ImgResponseDto>> imgIdMap = imgUrlList
                .stream().collect(Collectors.groupingBy(ImgResponseDto::getPostId));

        // postId별 majorName 리스트에 담기
        List<MajorDto.ResponseDto> majorList = postRepository.majorFilter(postIdCollect);
        Map<Long, List<MajorDto.ResponseDto>> majorIdMap = majorList
                .stream().collect(Collectors.groupingBy(MajorDto.ResponseDto::getPostId));

        // PostCategoryResponseDto에 imgUrl, major 컬렉션 묶어주기
        for (PostFilterResponseDto key : landingPagePost) {
            key.setImgUrl(Optional.ofNullable(imgIdMap.get(key.getPostId())).orElse(new ArrayList<>()));
        }
        for (PostFilterResponseDto key : landingPagePost) {
            key.setMajorList(Optional.ofNullable(majorIdMap.get(key.getPostId())).orElse(new ArrayList<>()));
        }
        data.put("data", landingPagePost);

        return data;

    }

    @Transactional
    public Map<String, Object> home(PostFilterRequestDto postFilterRequestDto, Pageable pageable) {

        // 페이징 및 카테고리별 필터 처리된 post들을 pagePost에 담기
        Page<PostFilterResponseDto> pagePost = postRepository.filterPagePost(postFilterRequestDto, pageable);

        Map<String, Object> data = new HashMap<>();

        // 카테고리별 필터 처리된 후 조회된 postId 값 모음
        List<Long> postIdCollect = pagePost.stream().map(PostFilterResponseDto::getPostId).collect(Collectors.toList());


        // postId별 imgUrl 리스트에 담기
        List<ImgResponseDto> imgUrlList = postRepository.imgFilter(postIdCollect);
        Map<Long, List<ImgResponseDto>> imgIdMap = imgUrlList
                .stream().collect(Collectors.groupingBy(ImgResponseDto::getPostId));

        // postId별 majorName 리스트에 담기
        List<MajorDto.ResponseDto> majorList = postRepository.majorFilter(postIdCollect);
        Map<Long, List<MajorDto.ResponseDto>> majorIdMap = majorList
                .stream().collect(Collectors.groupingBy(MajorDto.ResponseDto::getPostId));

        // PostCategoryResponseDto에 imgUrl, major 컬렉션 묶어주기
        for (PostFilterResponseDto key : pagePost) {
            key.setImgUrl(Optional.ofNullable(imgIdMap.get(key.getPostId())).orElse(new ArrayList<>()));
        }
        for (PostFilterResponseDto key : pagePost) {
            key.setMajorList(Optional.ofNullable(majorIdMap.get(key.getPostId())).orElse(new ArrayList<>()));
        }
        data.put("data", pagePost.getContent());

        return data;
    }

    // 검색 리스트 띄워주기 위한 post 전체 조회
    @Transactional
    public Map<String, Object> getAllPost() {

        List<PostAllResponseDto> list = new ArrayList<>();

        Map<String, Object> data = new HashMap<>();

        // 모든 포스트의 title 만 모아놓은 list
        List<String> titleList = new ArrayList<>();
        for (Post postTitle : postRepository.findAll()) {
            titleList.add(postTitle.getTitle());
        }

        // 모든 포스트의 nickname 만 모아놓은 list
        List<String> nicknameList = new ArrayList<>();
        for (Post postNickname : postRepository.findAll()) {
            nicknameList.add(postNickname.getUser().getNickname());
        }

        // 모든 포스트의 content 만 모아놓은 list
        List<String> contentList = new ArrayList<>();
        for (Post postContent : postRepository.findAll()) {
            contentList.add(postContent.getContent());
        }

        PostAllResponseDto postAll = new PostAllResponseDto(titleList, nicknameList, contentList);
        list.add(postAll);

        data.put("data", list);
        return data;
    }
}