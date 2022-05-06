package com.hanghae99.finalproject.comment.dto;

import com.hanghae99.finalproject.comment.model.Comment;
import com.hanghae99.finalproject.post.model.Post;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommentDto {

    @Getter
    public static class RequestDto {
        private Long postId;
        private String comment;
    }

    @Getter
    public static class CreateResponseDto {
        private Long commentId;

        public CreateResponseDto(Long commentId) {
            this.commentId = commentId;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class ResponseDto {
        private Long commentId;
        private Long userId;
        private String nickname;
        private String profileImg;
        private String comment;
        private String createdAt;

        public ResponseDto(Comment comment, Long userId, String nickname, String profileImg) {
            this.commentId = comment.getId();
            this.userId = userId;
            this.nickname = nickname;
            this.profileImg = profileImg;
            this.comment = comment.getComment();
            this.createdAt = formatter(comment.getCreatedAt());
        }

        public String formatter(LocalDateTime localDateTime) {
            return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(localDateTime);
        }
    }
}