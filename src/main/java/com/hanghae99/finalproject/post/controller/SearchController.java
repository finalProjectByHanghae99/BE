package com.hanghae99.finalproject.post.controller;



import com.hanghae99.finalproject.post.dto.PostDto;
import com.hanghae99.finalproject.post.dto.SearchConditionDto;
import com.hanghae99.finalproject.post.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;



@RequiredArgsConstructor
@RestController
public class SearchController {

    private final SearchService searchService;

    //검색 api
    @PostMapping("/post/search/{page}")
    public Page<PostDto.ResponseDto> searchList(@RequestBody SearchConditionDto searchConditionDto,
                                                @PathVariable int page) {
        return searchService.searchList(searchConditionDto, page);
    }

}


