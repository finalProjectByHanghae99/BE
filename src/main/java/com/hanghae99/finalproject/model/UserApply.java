package com.hanghae99.finalproject.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
@Entity
public class UserApply {
    //모집 신청자
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;
    //신청한 모집글 pk
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;
    //신청자
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    //모집 시 메시지 전달
    @Column
    private String message;

    //신청 상태 '수락' 시 false -> true
    @Column(columnDefinition = "boolean default false")
    private boolean isAccepted;

    //지원하는 전공
    @Column
    private String applyMajor;
}