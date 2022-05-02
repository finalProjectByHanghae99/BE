package com.hanghae99.finalprooject.dto;

import lombok.Getter;
import lombok.Setter;

public class MajorDto {

    @Getter
    @Setter
    public static class RequestDto {
        private String majorName;
        private Integer numOfPeopleSet;
        private Integer numOfPeopleApply;
    }
}