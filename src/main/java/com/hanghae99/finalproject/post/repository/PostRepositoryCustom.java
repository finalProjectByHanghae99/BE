package com.hanghae99.finalproject.post.repository;

import com.hanghae99.finalproject.img.ImgUrlDto;
import com.hanghae99.finalproject.post.dto.PostCategoryRequestDto;
import com.hanghae99.finalproject.post.dto.PostCategoryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostRepositoryCustom {

    Page<PostCategoryResponseDto> filterPagePost(PostCategoryRequestDto postCategoryRequestDto, Pageable pageable);

    List<ImgUrlDto> imgFilter(List<Long> postIdCollect);
}