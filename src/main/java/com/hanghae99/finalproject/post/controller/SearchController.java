package com.hanghae99.finalproject.post.controller;


import com.hanghae99.finalproject.post.dto.SearchRequestDto;
import com.hanghae99.finalproject.post.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RequiredArgsConstructor
@RestController
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/search/{page}")
    public Map<String, Object> search(@PathVariable int page,
                                    @RequestParam(required = false) String region,
                                    @RequestParam(required = false) String major,
                                    @RequestParam(required = false) String searchKey,
                                    @RequestParam(required = false) String searchValue) {

        SearchRequestDto searchRequestDto = new SearchRequestDto(region, major, searchKey, searchValue);
        return searchService.searchFilter(searchRequestDto, page);

    }
}