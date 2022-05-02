package com.hanghae99.finalprooject.service;

import com.hanghae99.finalprooject.dto.CommentDto;
import com.hanghae99.finalprooject.exception.ErrorCode;
import com.hanghae99.finalprooject.exception.PrivateException;
import com.hanghae99.finalprooject.model.Comment;
import com.hanghae99.finalprooject.repository.CommentRepository;
import com.hanghae99.finalprooject.repository.PostRepository;
import com.hanghae99.finalprooject.repository.UserRepository;
import com.hanghae99.finalprooject.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    // comment 등록
    @Transactional
    public void createComment(CommentDto.RequestDto requestDto, UserDetailsImpl userDetails) {

        convertCommentDto(commentRepository.save(
                Comment.createComment(
                        postRepository.findById(requestDto.getPostId()).orElseThrow(
                                () -> new PrivateException(ErrorCode.POST_NOT_FOUND)
                        ),
                        userRepository.findById(userDetails.getUser().getId()).orElseThrow(
                                () -> new PrivateException(ErrorCode.NOT_FOUND_USER_INFO)
                        ),
                        requestDto.getComment()
                ))
        );


//        Post post = postRepository.findById(postId).orElseThrow(
//                () -> new PrivateException(ErrorCode.POST_NOT_FOUND)
//        );
//
//        User user = userRepository.findByNickname(userDetails.getUser().getNickname()).orElseThrow(
//                () -> new PrivateException(ErrorCode.NOT_FOUND_USER_INFO)
//        );
//
//        Comment comment = new Comment(post, requestDto, user);
//        commentRepository.save(comment);
    }

    public static void convertCommentDto(Comment comment) {
        new CommentDto.ResponseDto(
                comment.getId(),
                comment.getUser().getId(),
                comment.getUser().getNickname(),
                comment.getUser().getProfileImg(),
                comment.getComment());
    }
}