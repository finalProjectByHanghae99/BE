package com.hanghae99.finalproject.post.controller;

import com.hanghae99.finalproject.post.dto.PostCategoryRequestDto;
import com.hanghae99.finalproject.post.service.PostCategoryService;
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
public class PostCategoryController {

    private final PostCategoryService postCategoryService;

    @GetMapping("/post/category/{page}")
    public Map<String, Object> home(@PathVariable int page,
                                    @RequestParam(required = false) String region,
                                    @RequestParam(required = false) String major) {

        PostCategoryRequestDto postCategoryRequestDto = new PostCategoryRequestDto(region, major);
        Pageable pageable = PageRequest.of(page, 8);

        return postCategoryService.home(postCategoryRequestDto, pageable);
    }
}