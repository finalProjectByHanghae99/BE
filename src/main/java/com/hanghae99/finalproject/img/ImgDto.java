package com.hanghae99.finalproject.img;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImgDto {
    private String imgName;
    private String imgUrl;
}