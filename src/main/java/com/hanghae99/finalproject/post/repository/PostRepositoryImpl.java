package com.hanghae99.finalproject.post.repository;

import com.hanghae99.finalproject.img.ImgResponseDto;
import com.hanghae99.finalproject.img.QImgResponseDto;
import com.hanghae99.finalproject.post.dto.PostCategoryRequestDto;
import com.hanghae99.finalproject.post.dto.PostCategoryResponseDto;
import com.hanghae99.finalproject.post.dto.QPostCategoryResponseDto;
import com.hanghae99.finalproject.post.model.Post;
import com.hanghae99.finalproject.user.dto.MajorDto;
import com.hanghae99.finalproject.user.dto.QMajorDto_ResponseDto;
import com.hanghae99.finalproject.user.model.QMajor;
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
import static com.hanghae99.finalproject.user.model.QMajor.major;
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
                ))
                .from(post)
                .leftJoin(post.majorList, major)
                .where(
                        regionContain(postCategoryRequestDto.getRegion()),
                        majorNameContain(postCategoryRequestDto.getMajor())
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
                        regionContain(postCategoryRequestDto.getRegion()),
                        majorNameContain(postCategoryRequestDto.getMajor())
                )
                .orderBy(post.createdAt.desc());
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
    private BooleanExpression regionContain(String region) {
        if (hasText(region)) {
            return post.region.contains(region);
        }
        return null;
    }

    // 분야별 조회
    private BooleanExpression majorNameContain(String major) {
        if (hasText(major)) {
            return QMajor.major.majorName.contains(major);
        }
        return null;
    }
}