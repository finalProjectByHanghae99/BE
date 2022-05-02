package com.hanghae99.finalprooject.dto;

import com.hanghae99.finalprooject.model.CurrentStatus;
import com.hanghae99.finalprooject.model.Post;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    public static class DetailDto {
        private Long postId;
        private Long userId;
        private String nickname;
        private String title;
        private String content;
        private String deadline;
        private CurrentStatus currentStatus;
        private String region;
        private String createdAt;
        private List<String> imgList;

        public DetailDto(Long postId, Post post, List<String> imgList) {
            this.postId = postId;
            this.userId = post.getUser().getId();
            this.nickname = post.getUser().getNickname();
            this.title = post.getTitle();
            this.content = post.getContent();
            this.deadline = post.getDeadline();
//            this.currentStatus = post.getCurrentStatus();
            this.region = post.getRegion();
            this.createdAt = formatter(post.getCreateAt());
            this.imgList = imgList;
        }

        public String formatter(LocalDateTime localDateTime) {
            return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(localDateTime);
        }
    }

    @Setter
    @Getter
    public static class PutRequestDto {
        private String title;
        private String content;
        private String deadline;
        private CurrentStatus currentStatus;
        private String region;
        private List<ImgUrlDto> imgUrl;
    }
}