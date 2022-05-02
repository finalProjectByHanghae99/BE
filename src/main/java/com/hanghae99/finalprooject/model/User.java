package com.hanghae99.finalprooject.model;

import lombok.*;
import lombok.NoArgsConstructor;

import javax.persistence.*;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String major;

    @Column
    private String intro;

    @Column
    private String profileImg;

    @Column
    private String link;

    @Column
    private int projectCount;

    @Column
    private int likeCount;
}