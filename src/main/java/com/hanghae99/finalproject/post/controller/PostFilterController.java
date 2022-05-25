package com.hanghae99.finalproject.post.controller;

import com.hanghae99.finalproject.post.dto.PostFilterRequestDto;
import com.hanghae99.finalproject.post.service.PostFilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PostFilterController {

    private final PostFilterService postFilterService;

    // post 랜딩 페이지 조회 API

    @GetMapping("/api/preview")
    public Map<String, Object> landingPage() {
        return postFilterService.landingPage();
    }

    // post 메인 조회 API

    @GetMapping("/post/filter/{page}")
    public Map<String, Object> home(@PathVariable int page,
                                    @RequestParam(required = false) String region,
                                    @RequestParam(required = false) String major,
                                    @RequestParam(required = false) String searchKey,
                                    @RequestParam(required = false) String searchValue) {

        PostFilterRequestDto postFilterRequestDto = new PostFilterRequestDto(region, major, searchKey, searchValue);
        Pageable pageable = PageRequest.of(page, 8);

        return postFilterService.home(postFilterRequestDto, pageable);
    }

    // post 전체 조회 API - 검색 목록
    @GetMapping("/api/posts")
    public Map<String, Object> getAll() {
        return postFilterService.getAllPost();
    }
}