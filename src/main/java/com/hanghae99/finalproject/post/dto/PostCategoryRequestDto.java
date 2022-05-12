package com.hanghae99.finalproject.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostCategoryRequestDto {
    private String region;
    private String major;
    private String searchKey; // 제목 / 유저네임/ 컨텐츠
    private String searchValue; // <      <      <
}