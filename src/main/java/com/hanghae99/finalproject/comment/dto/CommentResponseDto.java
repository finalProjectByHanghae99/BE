package com.hanghae99.finalproject.comment.dto;

import com.hanghae99.finalproject.comment.model.Comment;
import com.hanghae99.finalproject.user.dto.MyPageDto;
import com.hanghae99.finalproject.user.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
public class CommentResponseDto {

    private Long commentId;
    private String comment;
    private String createdAt;
    private Long userId;
    private String nickname;
    private String profileImg;

    public CommentResponseDto(Comment comment) {
        this.commentId = comment.getId();
        this.comment = comment.getComment();
        this.createdAt = formatter(comment.getCreatedAt());
        this.userId = comment.getUser().getId();
        this.nickname = comment.getUser().getNickname();
        this.profileImg = comment.getUser().getProfileImg();
    }

    public String formatter(LocalDateTime localDateTime) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(localDateTime);
    }

}