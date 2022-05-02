package com.hanghae99.finalprooject.dto.userDto;

import com.hanghae99.finalprooject.model.Img;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class MyPageDto {


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseDto{
        private Long userId;
        private String nickname;
        private String profileImg; // 프로필 이미지 링크
        private String intro;
        private List<Img> introImgList;
        private int projectCount;
        private int likeCount;


    }
}
