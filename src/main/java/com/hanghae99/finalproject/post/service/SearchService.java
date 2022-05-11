package com.hanghae99.finalproject.post.service;

import com.hanghae99.finalproject.post.dto.PostDto;
import com.hanghae99.finalproject.post.dto.SearchConditionDto;
import com.hanghae99.finalproject.post.dto.SearchPostDto;
import com.hanghae99.finalproject.post.repository.PostSearchRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SearchService {

    private final PostSearchRepositoryCustom postSearchRepositoryCustom;

    public Page<SearchPostDto> searchList(SearchConditionDto searchConditionDto, int page){


        Pageable pageable = createPageRequest(page);
        return postSearchRepositoryCustom.findAllBySearchCondition(pageable, searchConditionDto);
    }

    public PageRequest createPageRequest(int page){
        return PageRequest.of(page,8, Sort.by(Sort.Direction.DESC,"createAt"));
    }
}
