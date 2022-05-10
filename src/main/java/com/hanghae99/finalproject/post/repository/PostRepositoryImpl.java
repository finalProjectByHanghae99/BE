package com.hanghae99.finalproject.post.repository;

import com.hanghae99.finalproject.img.Img;
import com.hanghae99.finalproject.img.ImgUrlDto;
import com.hanghae99.finalproject.img.QImg;
import com.hanghae99.finalproject.img.QImgUrlDto;
import com.hanghae99.finalproject.post.dto.PostCategoryRequestDto;
import com.hanghae99.finalproject.post.dto.PostCategoryResponseDto;
import com.hanghae99.finalproject.post.dto.QPostCategoryResponseDto;
import com.hanghae99.finalproject.post.model.Post;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import javax.persistence.EntityManager;
import java.util.List;

import static com.hanghae99.finalproject.img.QImg.img;
import static com.hanghae99.finalproject.post.model.QPost.post;
import static com.hanghae99.finalproject.user.model.QUser.user;
import static org.springframework.util.StringUtils.hasText;

public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public PostRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<PostCategoryResponseDto> filterPagePost(PostCategoryRequestDto postCategoryRequestDto, Pageable pageable) {
        List<PostCategoryResponseDto> result = queryFactory
                .select(new QPostCategoryResponseDto(
                        post.id.as("postId"),
                        post.user.id.as("userId"),
                        post.user.nickname,
                        post.user.profileImg,
                        post.title,
                        post.deadline,
                        post.currentStatus,
                        post.region,
                        post.createdAt
//                        (Expression<? extends List<String>>) post.imgList,
//                        (Expression<? extends List<MajorDto.ResponseDto>>) post.majorList
                ))
                .from(post)
                .leftJoin(post.user, user)
                .where(
                        regionEq(postCategoryRequestDto.getRegion())
//                        majorNameEq(postCategoryRequestDto.getRecruitmentMajor())
                )
                .orderBy(post.createdAt.desc())     // 작성시간 기준 최신순 정렬
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Post> count = queryFactory
                .select(post)
                .from(post)
                .leftJoin(post.user, user)
                .where(
                        regionEq(postCategoryRequestDto.getRegion())
//                        majorNameEq(postCategoryRequestDto.getRecruitmentMajor())
                )
                .orderBy(post.createdAt.desc());
        return PageableExecutionUtils.getPage(result, pageable, count::fetchCount);
    }

    // 이미지 조회
    public List<ImgUrlDto> imgFilter(List<Long> postIdCollect) {
        return queryFactory
                .select(new QImgUrlDto(
                        img.imgUrl
                ))
                .from(img)
                .leftJoin(img.post, post)
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
//    private Predicate majorNameEq(String recruitmentMajor) {
//        if (hasText(recruitmentMajor)) {
//
//
//
//
//            return post.majorList.contains()
//        }
//        return null;
//    }


}
