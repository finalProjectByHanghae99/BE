package com.hanghae99.finalproject.img;

import com.hanghae99.finalproject.post.model.Post;
import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Img {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imgName;

    @Column(nullable = false)
    private String imgUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Post post;

    public Img(String imgName, String imgUrl, Post post) {
        this.imgName = imgName;
        this.imgUrl = imgUrl;
        this.post = post;
    }
}