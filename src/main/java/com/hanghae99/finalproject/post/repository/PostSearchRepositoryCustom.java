package com.hanghae99.finalproject.post.repository;


import com.hanghae99.finalproject.post.dto.*;
import com.hanghae99.finalproject.post.model.Post;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.hanghae99.finalproject.post.model.QPost.post;
import static com.hanghae99.finalproject.user.model.QUser.user;

@RequiredArgsConstructor
@Repository
public class PostSearchRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public Page<SearchPostDto> findAllBySearchCondition(Pageable pageable, SearchConditionDto searchConditionDto){


        List<SearchPostDto> results = queryFactory
                .select(new QSearchPostDto(
                        post.id.as("postId"),
                        post.user.id.as("userId"),
                        post.user.nickname,
                        post.user.profileImg,
                        post.title,
                        post.deadline,
                        post.currentStatus,
                        post.region,
                        post.createdAt
                ))
                .from(post)
                .where(searchKeywords(searchConditionDto.getSearchKey(), searchConditionDto.getSearchValue()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(post.createdAt.desc())
                .fetch();

        JPAQuery<Post> count = queryFactory
                .select(post)
                .from(post)
                .leftJoin(post.user,user)
                .where(
                        searchKeywords(searchConditionDto.getSearchKey(), searchConditionDto.getSearchValue())
                )
                .orderBy(post.createdAt.desc());

        return PageableExecutionUtils.getPage(results, pageable, count::fetchCount);
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
