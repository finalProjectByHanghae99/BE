package com.hanghae99.finalproject.post.service;

import com.hanghae99.finalproject.img.ImgRepository;
import com.hanghae99.finalproject.img.ImgUrlDto;
import com.hanghae99.finalproject.post.dto.PostCategoryRequestDto;
import com.hanghae99.finalproject.post.dto.PostCategoryResponseDto;
import com.hanghae99.finalproject.post.repository.PostRepository;
import com.hanghae99.finalproject.user.repository.MajorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostCategoryService {

    private final PostRepository postRepository;
    private final ImgRepository imgRepository;
    private final MajorRepository majorRepository;

//    @Transactional
//    public Map<String, Object> getPostCategoryFilter(PostCategoryRequestDto postCategoryRequestDto, Pageable pageable) {
//        Page<PostCategoryResponseDto> pagePost = postRepository.filterPagePost(postCategoryRequestDto, pageable);
//        Map<String, Object> data = new HashMap<>();
//        data.put("postList", pagePost.getContent());
//        data.put("last", pagePost.isLast());
//        data.put("totalPages", pagePost.getTotalPages());
//        return SuccessResult.success(data);
//    }
    @Transactional
    public Map<String, List<PostCategoryResponseDto>> home(PostCategoryRequestDto postCategoryRequestDto, Pageable pageable) {
        Map<String, List<PostCategoryResponseDto>> mapList = new HashMap<>();
//        List<PostCategoryResponseDto> list = new ArrayList<>();
        Page<PostCategoryResponseDto> pagePost = postRepository.filterPagePost(postCategoryRequestDto, pageable);
        List<Long> postIdCollect = pagePost.stream().map(PostCategoryResponseDto::getPostId).collect(Collectors.toList());
        for (PostCategoryResponseDto post : pagePost) {

            Map<Long, List<ImgUrlDto>> imgUrlList = postRepository.imgFilter(postIdCollect)
                    .stream()
                    .collect(Collectors.groupingBy(ImgUrlDto::getPostId));

//            List<String> imgUrlList = imgRepository.findAllByPost(postIdCollect.get(i))
//                    .stream()
//                    .map(Img::getImgUrl)
//                    .collect(Collectors.toList());

            String imgUrl;
            if (imgUrlList.isEmpty()) {
                imgUrl = "https://hyemco-butket.s3.ap-northeast-2.amazonaws.com/postDefaultImg.PNG";
            } else {
                imgUrl = String.valueOf(imgUrlList.get(0L));
            }

//            List<Major> findMajorByPost = majorRepository.findAllByPost(post);
//            List<MajorDto.ResponseDto> majorList = new ArrayList<>();
//            for (Major major : findMajorByPost) {
//                majorList.add(new MajorDto.ResponseDto(major));
//            }
//            PostDto.ResponseDto home = new PostDto.ResponseDto(post, imgUrl, majorList);
//            list.add(home);
        }
        mapList.put("data", post);
        return mapList;
    }
}