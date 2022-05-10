package com.hanghae99.finalproject.post.controller;

import com.hanghae99.finalproject.post.dto.PostCategoryRequestDto;
import com.hanghae99.finalproject.post.dto.PostCategoryResponseDto;
import com.hanghae99.finalproject.post.dto.PostDto;
import com.hanghae99.finalproject.post.service.PostCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PostCategoryController {

    private final PostCategoryService postCategoryService;

//    @GetMapping("/post/category")
//    public ResponseEntity<Map<String, List<PostCategoryResponseDto>>> postCategoryList() {
//        return postQueryRepository.postCategoryFilter();
//    }

//    @GetMapping("/post/category{page}")
//    public ResponseEntity<Map<String, List<PostCategoryResponseDto>>> postCategoryList(@PathVariable int page,
//                                                                                       @RequestParam(required = false) String region,
//                                                                                       @RequestParam(required = false) String recruitmentMajor) {
//
//        PostCategoryRequestDto postCategoryRequestDto = new PostCategoryRequestDto(region, recruitmentMajor);
//        Pageable pageable = PageRequest.of(page, 8);
//
////        return postQueryRepository.postCategoryFilter();
//
//        return new ResponseEntity<>(new StatusResponseDto("카테고리별 조회 성공", postCategoryService.getPostCategoryFilter(postCategoryRequestDto, pageable)));
//    }

//    @GetMapping("/post/category{page}")
//    public ResponseEntity<Map<String, Object>> postCategoryList(@PathVariable int page,
//                                                                @RequestParam(required = false) String region,
//                                                                @RequestParam(required = false) String recruitmentMajor) {
//
//        PostCategoryRequestDto postCategoryRequestDto = new PostCategoryRequestDto(region, recruitmentMajor);
//        Pageable pageable = PageRequest.of(page, 8);
//
//        return ResponseEntity.ok().body(postCategoryService.getPostCategoryFilter(postCategoryRequestDto, pageable));
//    }

    @GetMapping("/api/category/{page}")
    public Map<String, List<PostCategoryResponseDto>> home(@PathVariable int page,
                                                           @RequestParam(required = false) String region,
                                                           @RequestParam(required = false) String recruitmentMajor) {

        PostCategoryRequestDto postCategoryRequestDto = new PostCategoryRequestDto(region, recruitmentMajor);
        Pageable pageable = PageRequest.of(page, 8);

        return postCategoryService.home(postCategoryRequestDto, pageable);
    }




//    @GetMapping("/api/post/{postId}")
//    public ResponseEntity<Object> getDetailPost(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        PostDto.DetailDto detailDto = postService.getDetail(postId, userDetails);
//        return new ResponseEntity<>(new StatusResponseDto("게시물 상세 조회 성공", detailDto), HttpStatus.OK);
//    }
//
//    @GetMapping("/api/category")
//    public Map<String, List<PostDto.ResponseDto>> home() {
//        return postService.home();
//    }

}
