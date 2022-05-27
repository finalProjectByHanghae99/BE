package com.hanghae99.finalproject.comment.service;

import com.hanghae99.finalproject.comment.dto.CommentCreateResponseDto;
import com.hanghae99.finalproject.comment.dto.CommentRequestDto;
import com.hanghae99.finalproject.comment.model.Comment;
import com.hanghae99.finalproject.comment.repository.CommentRepository;
import com.hanghae99.finalproject.exception.ErrorCode;
import com.hanghae99.finalproject.exception.CustomException;
import com.hanghae99.finalproject.mail.dto.MailDto;
import com.hanghae99.finalproject.mail.service.MailService;
import com.hanghae99.finalproject.post.model.Post;
import com.hanghae99.finalproject.post.repository.PostRepository;
import com.hanghae99.finalproject.security.UserDetailsImpl;
import com.hanghae99.finalproject.sse.model.NotificationType;
import com.hanghae99.finalproject.sse.service.NotificationService;
import com.hanghae99.finalproject.user.model.User;
import com.hanghae99.finalproject.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final MailService mailService;
    private final NotificationService notificationService;

    // comment 등록
    @Transactional
    public CommentCreateResponseDto createComment(CommentRequestDto requestDto, UserDetailsImpl userDetails) {
        Post post = postRepository.findById(requestDto.getPostId()).orElseThrow(
                () -> new CustomException(ErrorCode.POST_NOT_FOUND)
        );

        // 로그인한 유저
        User user = loadUserByUserId(userDetails);

        Comment comment = new Comment(post, requestDto, user);
        commentRepository.save(comment);

        /*
         댓글이 달린 post 정보와 로그인한 user 정보가 일치하는 댓글들만 list 에 담고,
         가장 마지막에 달린 댓글 id를 commentId에 담아서 보내줌
         (방금 단 댓글이 해당 유저가 그 post 에 쓴 가장 마지막 댓글임을 이용)
         */
        List<Comment> findCommentByPost = commentRepository.findAllByPostAndUser(post, user);
        Long commentId = findCommentByPost.get(findCommentByPost.size() - 1).getId();

        //해당 댓글로 이동하는 url
        String Url = "https://www.everymohum.com/detail/"+post.getId();
        //댓글 생성 시 모집글 작성 유저에게 실시간 알림 전송 ,
        String content = post.getUser().getNickname()+"님! 프로젝트 댓글 알림이 도착했어요!";

        //본인의 게시글에 댓글을 남길때는 알림을 보낼 필요가 없다.
        if(!Objects.equals(userDetails.getUser().getId(), post.getUser().getId())) {
            notificationService.send(post.getUser(), NotificationType.REPLY, content, Url);
        }
        return new CommentCreateResponseDto(commentId);
    }

    // comment 수정
    @Transactional
    public void editComment(Long commentId, CommentRequestDto requestDto, UserDetailsImpl userDetails) {
        // commentId에 해당하는 댓글
        Comment comment = loadCommentByCommentId(commentId);

        // 로그인한 유저
        User user = loadUserByUserId(userDetails);

        // 본인 comment만 수정 가능
        if (!comment.getUser().equals(user)) {
            throw new CustomException(ErrorCode.COMMENT_UPDATE_WRONG_ACCESS);
        }

        comment.updateComment(requestDto);
    }

    // comment 삭제
    @Transactional
    public void deleteComment(Long commentId, UserDetailsImpl userDetails) {
        // commentId에 해당하는 댓글
        Comment comment = loadCommentByCommentId(commentId);

        // 로그인한 유저
        User user = loadUserByUserId(userDetails);

        // 본인 comment만 삭제 가능
        if (!comment.getUser().equals(user)) {
            throw new CustomException(ErrorCode.COMMENT_DELETE_WRONG_ACCESS);
        }
        commentRepository.deleteById(commentId);
    }

    // [예외 처리] 로그인한 유저 정보가 존배하지 않을 경우
    private User loadUserByUserId(UserDetailsImpl userDetails) {
        return  userRepository.findById(userDetails.getUser().getId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_INFO)
        );
    }

    // [예외 처리] commentId에 해당하는 댓글 없을 경우
    private Comment loadCommentByCommentId(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new CustomException(ErrorCode.COMMENT_NOT_FOUND)
        );
    }

}