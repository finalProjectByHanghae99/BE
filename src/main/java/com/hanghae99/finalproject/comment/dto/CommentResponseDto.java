package com.hanghae99.finalproject.comment.dto;

import com.hanghae99.finalproject.comment.model.Comment;
import com.hanghae99.finalproject.timeConversion.TimeConversion;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
        this.createdAt = TimeConversion.timeConversion(comment.getCreatedAt());
        this.userId = comment.getUser().getId();
        this.nickname = comment.getUser().getNickname();
        this.profileImg = comment.getUser().getProfileImg();
    }
}
