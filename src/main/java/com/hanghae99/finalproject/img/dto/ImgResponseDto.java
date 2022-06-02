package com.hanghae99.finalproject.img.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImgResponseDto {
    private Long postId;
    private String imgUrl;

    @QueryProjection
    public ImgResponseDto(Long postId, String imgUrl) {
        this.postId = postId;
        this.imgUrl = imgUrl;
    }
}