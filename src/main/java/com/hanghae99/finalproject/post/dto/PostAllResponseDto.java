package com.hanghae99.finalproject.post.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PostAllResponseDto {
    private List<String> titleList;
    private List<String> nicknameList;
    private List<String> contentList;

    public PostAllResponseDto(List<String> titleList, List<String> nicknameList, List<String> contentList) {
        this.titleList = titleList;
        this.nicknameList = nicknameList;
        this.contentList = contentList;
    }
}