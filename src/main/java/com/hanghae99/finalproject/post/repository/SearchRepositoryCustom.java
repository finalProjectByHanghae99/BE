package com.hanghae99.finalproject.post.repository;

import com.hanghae99.finalproject.post.dto.SearchRequestDto;
import com.hanghae99.finalproject.post.dto.SearchResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchRepositoryCustom {

    Page<SearchResponseDto> findPageDynamicQuery(SearchRequestDto searchRequestDto, Pageable pageable);
}
