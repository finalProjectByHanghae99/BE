package com.hanghae99.finalproject.sse.service;



import com.hanghae99.finalproject.exception.CustomException;
import com.hanghae99.finalproject.exception.ErrorCode;
import com.hanghae99.finalproject.post.model.Post;
import com.hanghae99.finalproject.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static com.hanghae99.finalproject.sse.controller.SseController.sseEmitters;


@Service
@RequiredArgsConstructor
public class SseService {
    private final PostRepository postRepository;

    public void notifyAddCommentEvent(Long postId){
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(ErrorCode.POST_NOT_FOUND)
        );
        // 게시글 작성자 pk
        Long userId = post.getUser().getId();


        if(sseEmitters.containsKey(userId)){
            SseEmitter sseEmitter = sseEmitters.get(userId);
            try{
                sseEmitter.send(SseEmitter.event().name("addComment").data("모집글에 댓글이 생성되었습니다."));

            }catch (Exception e){
                sseEmitters.remove(userId);
            }

        }



    }
}
