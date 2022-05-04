package com.hanghae99.finalproject.post.model;

import com.hanghae99.finalproject.comment.model.Comment;
import com.hanghae99.finalproject.img.Img;
import com.hanghae99.finalproject.post.dto.PostDto;
import com.hanghae99.finalproject.timeConversion.TimeStamped;
import com.hanghae99.finalproject.user.model.Major;
import com.hanghae99.finalproject.user.model.User;
import com.hanghae99.finalproject.user.model.UserApply;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity
public class Post extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 100, nullable = false)
    private String content;

    @Column(nullable = false)
    private String deadline;

    @Enumerated(EnumType.STRING)
    private CurrentStatus currentStatus;

    @Column(nullable = false)
    private String region;

    private String link;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Img> imgList = new ArrayList<>();


    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Major> majorList = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> commentList = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<UserApply> userApplyList = new ArrayList<>();

    @Builder
    public Post(String title, String content, String deadline, CurrentStatus currentStatus, String region, String link, User user, List<Img> imgList, List<Major> majorList) {
        this.title = title;
        this.content = content;
        this.deadline = deadline;
        this.currentStatus = currentStatus;
        this.region = region;
        this.link = link;
        this.user = user;
        this.imgList = imgList;
        for (Img img : imgList) {
            img.setPost(this);
        }
        this.majorList = majorList;
        for (Major major : majorList) {
            major.setPost(this);
        }
    }

    // post 수정
    public void updatePost(PostDto.PutRequestDto putRequestDto, List<Img> imgList) {
        this.title = putRequestDto.getTitle();
        this.content = putRequestDto.getContent();
        this.deadline = putRequestDto.getDeadline();
        this.region = putRequestDto.getRegion();
        this.link = putRequestDto.getLink();
        this.imgList = imgList;
        for (Img img : imgList) {
            img.setPost(this);
        }
    }
}