package com.hanghae99.finalproject.post.repository;

import com.hanghae99.finalproject.img.dto.ImgResponseDto;
import com.hanghae99.finalproject.img.dto.QImgResponseDto;
import com.hanghae99.finalproject.post.dto.PostFilterRequestDto;
import com.hanghae99.finalproject.post.dto.PostFilterResponseDto;
import com.hanghae99.finalproject.post.dto.QPostFilterResponseDto;
import com.hanghae99.finalproject.post.model.CurrentStatus;
import com.hanghae99.finalproject.post.model.Post;
import com.hanghae99.finalproject.user.dto.MajorDto;
import com.hanghae99.finalproject.user.dto.QMajorDto_ResponseDto;
import com.hanghae99.finalproject.user.model.QMajor;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import javax.persistence.EntityManager;
import java.util.List;


import static com.hanghae99.finalproject.img.model.QImg.img;
import static com.hanghae99.finalproject.post.model.QPost.post;
import static com.hanghae99.finalproject.user.model.QMajor.major;
import static org.springframework.util.StringUtils.hasText;

public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public PostRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    public List<PostFilterResponseDto> filterLandingPage() {
        return queryFactory
                .selectDistinct(new QPostFilterResponseDto(
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
                .where(post.currentStatus.eq(CurrentStatus.valueOf("ONGOING")))
                .orderBy(post.createdAt.asc())
                .offset(0)
                .limit(12)
                .fetch();
    }

    @Override
    public Page<PostFilterResponseDto> filterPagePost(PostFilterRequestDto postFilterRequestDto, Pageable pageable) {
        // 프로젝트 currentStatus 별로 나눠서 조회하기
        NumberExpression<Integer> roleRankPath = new CaseBuilder()
                .when(post.currentStatus.eq(CurrentStatus.valueOf("ONGOING"))).then(1)
                .when(post.currentStatus.eq(CurrentStatus.valueOf("RECRUITING_CLOSE"))).then(2)
                .otherwise(3);

        List<PostFilterResponseDto> result = queryFactory
                .selectDistinct(new QPostFilterResponseDto(
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
                        regionEq(postFilterRequestDto.getRegion()),
                        majorNameEq(postFilterRequestDto.getMajor()),
                        searchKeywords(postFilterRequestDto.getSearchKey(), postFilterRequestDto.getSearchValue())
                )
                .orderBy(roleRankPath.asc(),post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Post> count = queryFactory
                .selectDistinct(post)
                .from(post)
                .leftJoin(post.majorList, major)
                .where(
                        regionEq(postFilterRequestDto.getRegion()),
                        majorNameEq(postFilterRequestDto.getMajor()),
                        searchKeywords(postFilterRequestDto.getSearchKey(), postFilterRequestDto.getSearchValue())
                )
                .orderBy(roleRankPath.asc(),post.createdAt.desc());
        return PageableExecutionUtils.getPage(result, pageable, count::fetchCount);
    }

    // postId에 맞는 이미지 조회
    public List<ImgResponseDto> imgFilter(List<Long> postIdCollect) {
        return queryFactory
                .select(new QImgResponseDto(
                        post.id,
                        img.imgUrl
                ))
                .from(img)
                .leftJoin(img.post, post)
                .where(post.id.in(postIdCollect))
                .fetch();
    }

    // postId에 맞는 분야 조회
    public List<MajorDto.ResponseDto> majorFilter(List<Long> postIdCollect) {
        return queryFactory
                .select(new QMajorDto_ResponseDto(
                        post.id,
                        major.id,
                        major.majorName,
                        major.numOfPeopleSet,
                        major.numOfPeopleApply
                ))
                .from(major)
                .leftJoin(major.post, post)
                .where(post.id.in(postIdCollect))
                .fetch();
    }

    // 지역별 조회
    private BooleanExpression regionEq(String region) {
        if (hasText(region)) {
            return post.region.eq(region);
        }
        return null;
    }

    // 분야별 조회
    private BooleanExpression majorNameEq(String major) {
        if (hasText(major)) {
            return QMajor.major.majorName.eq(major);
        }
        return null;
    }

    // 검색 조회
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