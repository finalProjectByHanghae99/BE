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
    public static class ResponseDto{ ;
        private String nickname;
        private String profileImg;
        private String intro;
        private List<Img> introImgList;


    }
}
