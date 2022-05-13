
package com.hanghae99.finalproject.post.service;

import com.hanghae99.finalproject.img.ImgResponseDto;
import com.hanghae99.finalproject.post.dto.SearchRequestDto;
import com.hanghae99.finalproject.post.dto.SearchResponseDto;
import com.hanghae99.finalproject.post.repository.PostRepository;
import com.hanghae99.finalproject.post.repository.SearchRepositoryImpl;
import com.hanghae99.finalproject.user.dto.MajorDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final PostRepository postRepository;
    private final SearchRepositoryImpl searchRepository;

    @Transactional
    public Map<String, Object> searchFilter(SearchRequestDto searchRequestDto, int page) {

        Pageable pageable = createPageRequest(page);

        // 페이징 및 카테고리별 필터 처리된 post들을 pagePost에 담기
        Page<SearchResponseDto> pagePost = searchRepository.findPageDynamicQuery(searchRequestDto,pageable);

        Map<String, Object> data = new HashMap<>();

        // 카테고리별 필터 처리된 후 조회된 postId 값 모음
        List<Long> postIdCollect = pagePost.stream().map(SearchResponseDto::getPostId).collect(Collectors.toList());


        // postId별 imgUrl 리스트에 담기
        List<ImgResponseDto> imgUrlList = postRepository.imgFilter(postIdCollect);
        Map<Long, List<ImgResponseDto>> imgIdMap = imgUrlList
                .stream().collect(Collectors.groupingBy(ImgResponseDto::getPostId));

        // postId별 majorName 리스트에 담기
        List<MajorDto.ResponseDto> majorList = postRepository.majorFilter(postIdCollect);
        Map<Long, List<MajorDto.ResponseDto>> majorIdMap = majorList
                .stream().collect(Collectors.groupingBy(MajorDto.ResponseDto::getPostId));

        // PostCategoryResponseDto에 imgUrl, major 컬렉션 묶어주기
        for (SearchResponseDto key : pagePost) {
            key.setImgUrl(Optional.ofNullable(imgIdMap.get(key.getPostId())).orElse(new ArrayList<>()));
        }
        for (SearchResponseDto key : pagePost) {
            key.setMajorList(Optional.ofNullable(majorIdMap.get(key.getPostId())).orElse(new ArrayList<>()));
        }
        data.put("data", pagePost.getContent());

        return data;
    }

    public PageRequest createPageRequest(int page){
        return PageRequest.of(page,8, Sort.by(Sort.Direction.DESC,"createAt"));
    }

}

