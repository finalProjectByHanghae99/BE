package com.hanghae99.finalproject.post.dto;


import com.hanghae99.finalproject.post.model.CurrentStatus;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class SearchPostDto {

    private Long postId;
    private Long userId;
    private String nickname;
    private String profileImg;
    private String title;
    private String deadline;
    private CurrentStatus currentStatus;
    private String region;
    private String createdAt;

    @QueryProjection
    public SearchPostDto(Long postId, Long userId, String nickname, String profileImg, String title,
                                   String deadline, CurrentStatus currentStatus, String region, LocalDateTime createdAt) {
        this.postId = postId;
        this.userId = userId;
        this.nickname = nickname;
        this.profileImg = profileImg;
        this.title = title;
        this.deadline = deadline;
        this.currentStatus = currentStatus;
        this.region = region;
        this.createdAt = formatter(createdAt);
    }

    public static String formatter(LocalDateTime localDateTime) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(localDateTime);
    }
}
