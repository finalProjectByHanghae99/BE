package com.hanghae99.finalproject.post.repository;


import com.hanghae99.finalproject.post.dto.PostDto;
import com.hanghae99.finalproject.post.dto.SearchConditionDto;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import static com.hanghae99.finalproject.post.model.QPost.post;

@RequiredArgsConstructor
@Repository
public class PostSearchRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public Page<PostDto.ResponseDto> findAllBySearchCondition(Pageable pageable, SearchConditionDto searchConditionDto){


        QueryResults<PostDto.ResponseDto> results = queryFactory
                .select(Projections.constructor(PostDto.ResponseDto.class,post))
                .from(post)
                .where(searchKeywords(searchConditionDto.getSearchKey(), searchConditionDto.getSearchValue()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(post.createdAt.desc())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    //카테고리 조건을 포함시켜야함.

    private BooleanExpression searchKeywords(String sk, String sv) {
        if("nickname".equals(sk)) {
            if(StringUtils.hasLength(sv)) {
                return post.user.nickname.contains(sv);
            }
        } else if ("title".equals(sk)) {
            if(StringUtils.hasLength(sv)) {
                return post.title.contains(sv);
            }
        } else if ("content".equals(sk)) {
            if(StringUtils.hasLength(sv)) {
                return post.content.contains(sv);
            }
        }

        return null;
    }
}
