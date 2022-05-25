package com.hanghae99.finalproject.post.dto;

import com.hanghae99.finalproject.comment.dto.CommentResponseDto;
import com.hanghae99.finalproject.img.ImgUrlDto;
import com.hanghae99.finalproject.post.model.CurrentStatus;
import com.hanghae99.finalproject.post.model.Post;
import com.hanghae99.finalproject.user.dto.MajorDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class PostDto {

    @Getter
    @Setter
    public static class RequestDto {
        private String title;
        private String content;
        private String deadline;
        private String region;
        private String link;
        private List<MajorDto.RequestDto> majorList;
    }

    @Getter
    @NoArgsConstructor
    public static class DetailDto {
        private String userStatus;
        private Long postId;
        private Long userId;
        private String nickname;
        private String profileImg;
        private String title;
        private String content;
        private String deadline;
        private CurrentStatus currentStatus;
        private String region;
        private LocalDateTime createdAt;
        private String link;
        private List<String> imgList;
        private List<CommentResponseDto> commentList;
        private List<MajorDto.ResponseDto> majorList;

        public DetailDto(String userStatus , Long postId, Post post, List<String> imgList, List<CommentResponseDto> commentList, List<MajorDto.ResponseDto> majorList) {
            this.userStatus = userStatus;
            this.postId = postId;
            this.userId = post.getUser().getId();
            this.nickname = post.getUser().getNickname();
            this.profileImg = post.getUser().getProfileImg();
            this.title = post.getTitle();
            this.content = post.getContent();
            this.deadline = post.getDeadline();
            this.currentStatus = post.getCurrentStatus();
            this.region = post.getRegion();
            this.createdAt = post.getCreatedAt();
            this.link = post.getLink();
            if (imgList.isEmpty()) {
                this.imgList = Collections.singletonList("https://mohum.s3.ap-northeast-2.amazonaws.com/mohum-src/detail_default.png");
            } else {
                this.imgList = imgList;
            }
            this.commentList = commentList;
            this.majorList = majorList;
        }
    }

    @Setter
    @Getter
    public static class PutRequestDto {
        private String title;
        private String content;
        private String deadline;
        private String region;
        private String link;
        private List<ImgUrlDto> imgUrl;
    }
}