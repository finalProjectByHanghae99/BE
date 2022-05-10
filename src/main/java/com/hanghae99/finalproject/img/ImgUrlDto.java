package com.hanghae99.finalproject.img;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

@Data
@Builder
public class ImgUrlDto {
    private Long postId;
    private String imgUrl;

    @QueryProjection
    public ImgUrlDto(Long postId, String imgUrl) {
        this.postId = postId;
        this.imgUrl = imgUrl;
    }
}