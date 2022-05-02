package com.hanghae99.finalprooject.model;

import com.hanghae99.finalprooject.dto.CommentDto;
import com.hanghae99.finalprooject.exception.ErrorCode;
import com.hanghae99.finalprooject.exception.PrivateException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Comment extends TimeStamped{

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
    public static Comment createComment(Post post, User user, String comment) {
        if (!StringUtils.hasText(comment)) {
            throw new PrivateException(ErrorCode.COMMENT_WRONG_INPUT);
        }

        Comment comments = new Comment();
        comments.post = post;
        comments.user = user;
        comments.comment = comment;
        return comments;
    }

    public void updateComment(CommentDto.RequestDto requestDto) {
        if (!StringUtils.hasText(requestDto.getComment())) {
            throw new PrivateException(ErrorCode.COMMENT_WRONG_INPUT);
        }
        this.comment = requestDto.getComment();
    }
}