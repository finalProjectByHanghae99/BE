package com.hanghae99.finalprooject.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hanghae99.finalprooject.model.CurrentStatus;
import lombok.Getter;
import lombok.Setter;

public class PostDto {

    @Getter
    @Setter
    public static class RequestDto {
        private String title;
        private String content;
        private String deadline;
        private CurrentStatus currentStatus;
        private String region;
        private String category;

        @JsonCreator
        public RequestDto(
                @JsonProperty("title") String title,
                @JsonProperty("content") String content,
                @JsonProperty("deadline") String deadline,
                @JsonProperty("currentStatus") String currentStatus,
                @JsonProperty("region") String region,
                @JsonProperty("category") String category

        ){
            this.title = title;
            this.content = content;
            this.deadline = deadline;
            this.currentStatus = CurrentStatus.valueOf(currentStatus);
            this.region = region;
            this.category = category;
        }
    }
}