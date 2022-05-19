package com.hanghae99.finalproject.mail.dto;

import com.hanghae99.finalproject.post.model.Post;
import com.hanghae99.finalproject.user.model.User;
import com.hanghae99.finalproject.user.model.UserApply;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class MailDto {

    private Long toUserId;
    private String toEmail;
    private String toNickname;
    private String toProfileImg;
    private String fromNickname;
    private String fromProfileImg;
    private Long postId;
    private String postTitle;

    public MailDto(UserApply userApply) {
        this.toEmail = userApply.getPost().getUser().getEmail();
        this.toNickname = userApply.getPost().getUser().getNickname();
        this.fromNickname = userApply.getUser().getNickname();
        this.fromProfileImg = userApply.getUser().getProfileImg();
        this.postId = userApply.getPost().getId();
    }

    public MailDto(User user, Post post) {
        this.toEmail = user.getEmail();
        this.toNickname = user.getNickname();
        this.toProfileImg = user.getProfileImg();
        this.fromNickname = post.getUser().getNickname();
        this.fromProfileImg = post.getUser().getProfileImg();
        this.postId = post.getId();
    }
}