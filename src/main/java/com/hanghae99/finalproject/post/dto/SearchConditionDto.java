package com.hanghae99.finalproject.post.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchConditionDto {
    private String searchKey; // 타입과
    private String searchValue;   //내용

    public SearchConditionDto(String searchKey, String searchValue) {
        this.searchKey = searchKey;
        this.searchValue = searchValue;
    }
}
