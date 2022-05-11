package com.hanghae99.finalproject.comment.dto;

import lombok.Getter;

@Getter
public class CommentRequestDto {

    private Long postId;
    private String comment;
}