package com.hanghae99.finalproject.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostFilterRequestDto {
    private String region;
    private String major;
    private String searchKey; // 타입과
    private String searchValue;   //내용
}