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
    private String fromNickname;
    private Long postId;
    private String postTitle;
    private String message;
    private String profileImg;
    private List<User> toUserList;

    public MailDto(UserApply userApply) {
        this.toUserId = userApply.getPost().getUser().getId();
        this.toEmail = userApply.getPost().getUser().getEmail();
        this.toNickname = userApply.getPost().getUser().getNickname();
        this.fromNickname = userApply.getUser().getNickname();
        this.postId = userApply.getPost().getId();
        this.postTitle = userApply.getPost().getTitle();
        this.message = userApply.getMessage();
    }

    public MailDto(User user, Post post) {
        this.toUserId = user.getId();
        this.toEmail = user.getEmail();
        this.toNickname = user.getNickname();
        this.fromNickname = post.getUser().getNickname();
        this.postId = post.getId();
        this.postTitle = post.getTitle();
    }
}