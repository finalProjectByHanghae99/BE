package com.hanghae99.finalproject.post.controller;



import com.hanghae99.finalproject.post.dto.PostDto;
import com.hanghae99.finalproject.post.dto.SearchConditionDto;
import com.hanghae99.finalproject.post.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
public class SearchController {

    private final SearchService searchService;

    //검색 api
    @GetMapping("/post/search/{page}")
    public Page<PostDto.ResponseDto> searchList( @RequestParam(required = false) String searchKey,
                                                 @RequestParam(required = false) String searchValue,
                                                 @PathVariable int page) {
        SearchConditionDto searchConditionDto = new SearchConditionDto(searchKey,searchValue);
        return searchService.searchList(searchConditionDto, page);
    }

}


