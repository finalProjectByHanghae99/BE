package com.hanghae99.finalprooject.dto;

import lombok.*;

public class CommentDto {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RequestDto {
        private Long postId;
        private String comment;
    }
    @Data
    @NoArgsConstructor
    public static class ResponseDto {
        private Long id;
        private Long userId;
        private String nickname;
        private String profileImg;
        private String comment;

        @Builder
        public ResponseDto(Long id, Long userId, String nickname, String profileImg, String comment) {
            this.id = id;
            this.userId = userId;
            this.nickname = nickname;
            this.profileImg = profileImg;
            this.comment = comment;
        }
    }
}