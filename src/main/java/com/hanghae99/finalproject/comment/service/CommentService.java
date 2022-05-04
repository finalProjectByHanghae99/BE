package com.hanghae99.finalproject.comment.service;

import com.hanghae99.finalproject.comment.dto.CommentDto;
import com.hanghae99.finalproject.exception.ErrorCode;
import com.hanghae99.finalproject.exception.PrivateException;
import com.hanghae99.finalproject.comment.model.Comment;
import com.hanghae99.finalproject.post.model.Post;
import com.hanghae99.finalproject.user.model.User;
import com.hanghae99.finalproject.comment.repository.CommentRepository;
import com.hanghae99.finalproject.post.repository.PostRepository;
import com.hanghae99.finalproject.user.repository.UserRepository;
import com.hanghae99.finalproject.security.UserDetailsImpl;
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
        Post post = postRepository.findById(requestDto.getPostId()).orElseThrow(
                () -> new PrivateException(ErrorCode.POST_NOT_FOUND)
        );

        User user = userRepository.findByNickname(userDetails.getUser().getNickname()).orElseThrow(
                () -> new PrivateException(ErrorCode.NOT_FOUND_USER_INFO)
        );

        Comment comment = new Comment(post, requestDto, user);
        commentRepository.save(comment);
    }

    // comment 수정
    @Transactional
    public void editComment(Long commentId, CommentDto.RequestDto requestDto, UserDetailsImpl userDetails) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new PrivateException(ErrorCode.COMMENT_NOT_FOUND)
        );

        User user = userRepository.findByNickname(userDetails.getUser().getNickname()).orElseThrow(
                () -> new PrivateException(ErrorCode.NOT_FOUND_USER_INFO)
        );

        // 본인 comment만 수정 가능
        if (!comment.getUser().equals(user)) {
            throw new PrivateException(ErrorCode.COMMENT_UPDATE_WRONG_ACCESS);
        }

        comment.updateComment(requestDto);
    }

    // comment 삭제
    @Transactional
    public void deleteComment(Long commentId, UserDetailsImpl userDetails) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new PrivateException(ErrorCode.COMMENT_NOT_FOUND)
        );

        User user = userRepository.findByNickname(userDetails.getUser().getNickname()).orElseThrow(
                () -> new PrivateException(ErrorCode.NOT_FOUND_USER_INFO)
        );

        // 본인 comment만 삭제 가능
        if (!comment.getUser().equals(user)) {
            throw new PrivateException(ErrorCode.COMMENT_DELETE_WRONG_ACCESS);
        }
        commentRepository.deleteById(commentId);
    }
}