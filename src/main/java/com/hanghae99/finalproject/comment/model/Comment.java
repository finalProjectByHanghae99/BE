package com.hanghae99.finalproject.comment.model;

import com.hanghae99.finalproject.comment.dto.CommentRequestDto;
import com.hanghae99.finalproject.exception.ErrorCode;
import com.hanghae99.finalproject.exception.CustomException;
import com.hanghae99.finalproject.post.model.Post;
import com.hanghae99.finalproject.sse.dto.NotificationRequestDto;
import com.hanghae99.finalproject.sse.model.NotificationType;
import com.hanghae99.finalproject.timeConversion.TimeStamped;
import com.hanghae99.finalproject.user.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.StringUtils;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Comment extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(length = 100,nullable = false)
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="user_id")
    private User user;

    // comment 등록
    public Comment(Post post, CommentRequestDto requestDto, User user) {
        if (!StringUtils.hasText(requestDto.getComment())) {
            throw new CustomException(ErrorCode.COMMENT_WRONG_INPUT);
        }

        this.post = post;
        this.comment = requestDto.getComment();
        this.user = user;
    }

    // comment 삭제
    public void updateComment(CommentRequestDto requestDto) {
        if (!StringUtils.hasText(requestDto.getComment())) {
            throw new CustomException(ErrorCode.COMMENT_WRONG_INPUT);
        }
        this.comment = requestDto.getComment();
    }

    public void publishEvent(ApplicationEventPublisher eventPublisher, NotificationType notificationType){
        eventPublisher.publishEvent(new NotificationRequestDto(post.getUser(),notificationType,
                notificationType.makeContent(post.getTitle()),notificationType.makeUrl(post.getId())));

    }

}