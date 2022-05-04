package com.hanghae99.finalproject.dto.userDto;


import com.hanghae99.finalproject.dto.ImgUrlDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


public class MyPageDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestDto{

        private String profileImg; // 프로필 이미지 링크 -> Default 이미지 전달.
        private String nickname; //닉네임
        private String intro; // 자기 소개
        private String portfolioLink; //포트폴리오 링크
        private String major;
        private List<ImgUrlDto> currentImgUrl; //변경[삭제]을 원하는 현재 ImgUrl list


    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseDto{
        private Long userId; //해당 유저 Id
        private String nickname; //닉네임
        private String profileImg; // 프로필 이미지 링크 -> Default 이미지 전달.
        private String intro; // 자기 소개
        private String portfolioLink; //포트폴리오 링크
        private List<Map<Long,String>> userPortfolioImgList; //유저 포트폴리오 이미지리스트
        private int projectCount;// 해결한 프로젝트 카운트
        private int likeCount; // 좋아요 갯수 .

    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor //마이페이지 신청중 Dto
    public static class AppliedResponseDto{
        private Long postId;
        private Long userId;
        private String nickname;
        private String title;
        private LocalDateTime createAt;



    }
//    @Data
//    @Builder
//    @NoArgsConstructor
//    @AllArgsConstructor
//
//    public static class RecruitResponseDto{
//        private Long postId;
//        private Long userId;
//        private String nickname;
//        private String title;
//        private LocalDateTime createAt;
//        privaate List<>



   // }


}
