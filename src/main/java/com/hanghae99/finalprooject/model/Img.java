package com.hanghae99.finalprooject.model;

import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
public class Img {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(nullable = false)
    private String imgUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Post post;

    public Img(String imgUrl, Post post) {
        this.imgUrl = imgUrl;
        this.post = post;
    }
}