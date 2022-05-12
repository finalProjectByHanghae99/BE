package com.hanghae99.finalproject.post.dto;

import com.hanghae99.finalproject.img.ImgResponseDto;
import com.hanghae99.finalproject.post.model.CurrentStatus;
import com.hanghae99.finalproject.timeConversion.TimeConversion;
import com.hanghae99.finalproject.user.dto.MajorDto;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
public class PostFilterResponseDto {

    private Long postId;
    private Long userId;
    private String nickname;
    private String profileImg;
    private String title;
    private String deadline;
    private CurrentStatus currentStatus;
    private String region;
    private String createdAt;
    private List<ImgResponseDto> imgUrl;
    private List<MajorDto.ResponseDto> majorList;

    @QueryProjection
    public PostFilterResponseDto(Long postId, Long userId, String nickname, String profileImg, String title,
                                 String deadline, CurrentStatus currentStatus, String region, LocalDateTime createdAt) {
        this.postId = postId;
        this.userId = userId;
        this.nickname = nickname;
        this.profileImg = profileImg;
        this.title = title;
        this.deadline = deadline;
        this.currentStatus = currentStatus;
        this.region = region;
        this.createdAt = TimeConversion.timeConversion(createdAt);
    }
}