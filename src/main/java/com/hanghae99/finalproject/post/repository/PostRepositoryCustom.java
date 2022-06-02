package com.hanghae99.finalproject.post.repository;

import com.hanghae99.finalproject.img.dto.ImgResponseDto;
import com.hanghae99.finalproject.post.dto.PostFilterRequestDto;
import com.hanghae99.finalproject.post.dto.PostFilterResponseDto;
import com.hanghae99.finalproject.user.dto.MajorDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostRepositoryCustom {

    List<PostFilterResponseDto> filterLandingPage();

    Page<PostFilterResponseDto> filterPagePost(PostFilterRequestDto postFilterRequestDto, Pageable pageable);

    List<ImgResponseDto> imgFilter(List<Long> postIdCollect);

    List<MajorDto.ResponseDto> majorFilter(List<Long> postIdCollect);
}