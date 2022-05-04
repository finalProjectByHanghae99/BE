package com.hanghae99.finalproject.user.service;

import com.hanghae99.finalproject.user.dto.MajorDto;
import com.hanghae99.finalproject.exception.ErrorCode;
import com.hanghae99.finalproject.exception.PrivateException;
import com.hanghae99.finalproject.user.model.Major;
import com.hanghae99.finalproject.post.model.Post;
import com.hanghae99.finalproject.user.repository.MajorRepository;
import com.hanghae99.finalproject.post.repository.PostRepository;
import com.hanghae99.finalproject.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserApplyService {

    private final PostRepository postRepository;
    private final MajorRepository majorRepository;

    // 협업 신청
    @Transactional
    public void apply(Long postId, UserDetailsImpl userDetails) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new PrivateException(ErrorCode.POST_NOT_FOUND)
        );

        Long loginUserId = userDetails.getUser().getId();

        List<Major> findMajorByPost = majorRepository.findAllByPost(post);
        List<MajorDto.ResponseDto> majorList = new ArrayList<>();
        for (Major major : findMajorByPost) {
            majorList.add(new MajorDto.ResponseDto(major));
        }


        for (int i = 0 ; i < majorList.size(); i++) {
            System.out.println("majorList 뭐가 있니= " + majorList);
        }
    }
}