package com.hanghae99.finalproject.user.dto;


import com.hanghae99.finalproject.img.ImgUrlDto;
import com.hanghae99.finalproject.post.model.CurrentStatus;
import com.hanghae99.finalproject.user.model.UserApply;
import lombok.*;

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
        private String email;
        private String nickname; //닉네임
        private String profileImg; // 프로필 이미지 링크 -> Default 이미지 전달.
        private String intro; // 자기 소개
        private String major;
        private String portfolioLink; //포트폴리오 링크
        private List<String> userPortfolioImgList; //유저 포트폴리오 이미지리스트
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
        private CurrentStatus status;



    }
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor //마이페이지 모집중 Dto
    public static class RecruitResponseDto{
        private Long postId;
        private Long userId;
        private String nickname;
        private String title;
        private LocalDateTime createAt;
        private List<MyPageDto.ResponseEntityToUserApply> userApplyList;



    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor //마이페이지 모집중 신청자 Dto
    public static class ApplyUserList{
        private Long userId;
        private String profileImg;
        private String nickname;
        private String message;
        private String applyMajor;
        private int likePoint;
        private int AcceptedStatus;
        private int projectCount;


    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor //마이페이지 모집마감 Dto
    public static class RecruitOverList{
        private Long postId;
        private String title;
        private String nickname;
        private LocalDateTime createdAt;
        private List<MyPageDto.ResponseEntityToUserApply> userApplyList;


    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor // 팀원 리뷰 -> 팀원명단
    public static class RecruitUserList{
        private Long userId;
        private String nickname;
        private String profileImg;
    }

    @Data
    @Builder
    public static class RecruitPostUser {

        private ResponsePostToUserApply postUser;
        private List<MyPageDto.RecruitUserList> recruitUserList;

        public RecruitPostUser(List<MyPageDto.RecruitUserList> recruitUserList){
            this.recruitUserList = recruitUserList;
        }
        public RecruitPostUser(ResponsePostToUserApply postUser,  List<MyPageDto.RecruitUserList> recruitUserList) {
            this.postUser = postUser;
            this.recruitUserList = recruitUserList;
        }
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestUserRate{
        private int point;
        private Long receiverId;
        private Long postId;

    }


    @Setter
    @Getter
    public static class ResponseEntityToDto {

        private List<MajorDto.ResponseDto> majorList;
        private List<MyPageDto.ApplyUserList> applyUserLists;

        public ResponseEntityToDto(List<MajorDto.ResponseDto> majorList,List<MyPageDto.ApplyUserList> appliedResponseDtoList) {
            this.majorList = majorList;
            this.applyUserLists = appliedResponseDtoList;
        }
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseEntityToUserApply{

        private Long id;
        private Long postId;
        private Long userId;
        private String profileImg;
        private String nickname;
        private String message;
        private int isAccepted;
        private String applyMajor;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponsePostToUserApply {

        private Long userId;
        private String nickname;
        private String profileImg;


    }

}

















