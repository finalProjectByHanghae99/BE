package com.hanghae99.finalproject.post.repository;


import com.hanghae99.finalproject.post.dto.QSearchResponseDto;
import com.hanghae99.finalproject.post.dto.SearchRequestDto;
import com.hanghae99.finalproject.post.dto.SearchResponseDto;
import com.hanghae99.finalproject.post.model.Post;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.hanghae99.finalproject.post.model.QPost.post;
import static com.hanghae99.finalproject.user.model.QMajor.major;
import static org.springframework.util.StringUtils.hasText;

@Repository
public class SearchRepositoryImpl implements SearchRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public SearchRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<SearchResponseDto> findPageDynamicQuery(SearchRequestDto searchRequestDto, Pageable pageable){
        List<SearchResponseDto> result = queryFactory
                .selectDistinct(new QSearchResponseDto(
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
                .leftJoin(post.majorList, major)
                .where(
                        regionEq(searchRequestDto.getRegion()),
                        majorEq(searchRequestDto.getMajor()),
                        searchKeywords(searchRequestDto.getSearchKey(), searchRequestDto.getSearchValue())
                )
                .orderBy(post.createdAt.desc())     // 작성시간 기준 최신순 정렬
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Post> count = queryFactory
                .selectDistinct(post)
                .from(post)
                .leftJoin(post.majorList, major)
                .where(
                        regionEq(searchRequestDto.getRegion()),
                        majorEq(searchRequestDto.getMajor()),
                        searchKeywords(searchRequestDto.getSearchKey(), searchRequestDto.getSearchValue())
                )
                .orderBy(post.createdAt.desc());

        return PageableExecutionUtils.getPage(result, pageable, count::fetchCount);



    }

    private BooleanExpression majorEq(String majorCond){
        return majorCond != null ? major.majorName.eq(majorCond) : null;
    }
    private BooleanExpression regionEq(String regionCond){

        return regionCond != null ? post.region.eq(regionCond) : null;
    }

    private BooleanExpression searchKeywords(String sk, String sv) {
        if("nickname".equals(sk)) {
            if(hasText(sv)) {
                return post.user.nickname.contains(sv);
            }
        } else if ("title".equals(sk)) {
            if(hasText(sv)) {
                return post.title.contains(sv);
            }
        } else if ("content".equals(sk)) {
            if(hasText(sv)) {
                return post.content.contains(sv);
            }
        }
        return null;

    }

}
