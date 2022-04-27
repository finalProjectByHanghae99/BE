package com.hanghae99.finalprooject.dto;

import com.hanghae99.finalprooject.model.CurrentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class PostDto {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class RequestDto {
        private String title;
        private String content;
        private String deadline;
        private CurrentStatus currentStatus;
        private String region;
        private String category;
    }
}