package com.hanghae99.finalproject.user.dto;

import com.hanghae99.finalproject.user.model.Major;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class MajorDto {

    @Getter
    @Setter
    public static class RequestDto {
        private String majorName;
        private Integer numOfPeopleSet;
        private Integer numOfPeopleApply;
    }

    @Getter
    @NoArgsConstructor
    public static class ResponseDto {
        private Long postId;
        private Long majorId;
        private String majorName;
        private Integer numOfPeopleSet;
        private Integer numOfPeopleApply;


        public ResponseDto(Major major) {
            this.majorId = major.getId();
            this.majorName = major.getMajorName();
            this.numOfPeopleSet = major.getNumOfPeopleSet();
            this.numOfPeopleApply = major.getNumOfPeopleApply();
        }

        @QueryProjection
        public ResponseDto(Long postId, Long majorId, String majorName, Integer numOfPeopleSet, Integer numOfPeopleApply) {
            this.postId = postId;
            this.majorId = majorId;
            this.majorName = majorName;
            this.numOfPeopleSet = numOfPeopleSet;
            this.numOfPeopleApply = numOfPeopleApply;
        }
    }
}