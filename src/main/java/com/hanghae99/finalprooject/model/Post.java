package com.hanghae99.finalprooject.model;

import com.hanghae99.finalprooject.dto.PostDto;
import lombok.AllArgsConstructor;
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
    @Column
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
    private String category;

    @Column(nullable = false)
    private String region;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL)
    private List<Comment> commentList = new ArrayList<>();

    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL)
    private List<Img> imgList = new ArrayList<>();

    @Builder
    public Post(String title, String content, String deadline, CurrentStatus currentStatus, String region, String category, User user, List<Img> imgList) {
        this.title = title;
        this.content = content;
        this.deadline = deadline;
        this.currentStatus = currentStatus;
        this.region = region;
        this.category = category;
        this.user = user;
        this.imgList = imgList;
        for (Img img : imgList) {
            img.setPost(this);
        }
    }

    // post 수정
    public void updatePost(PostDto.PutRequestDto putRequestDto, List<Img> imgList) {
        this.title = putRequestDto.getTitle();
        this.content = putRequestDto.getContent();
        this.deadline = putRequestDto.getDeadline();
        this.currentStatus = putRequestDto.getCurrentStatus();
        this.region = putRequestDto.getRegion();
        this.category = putRequestDto.getCategory();
        this.imgList = imgList;
        for (Img img : imgList) {
            img.setPost(this);
        }
    }
}