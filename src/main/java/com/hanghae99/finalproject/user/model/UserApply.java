package com.hanghae99.finalproject.user.model;

import com.hanghae99.finalproject.post.model.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
@Entity
public class UserApply {
    //모집 신청자
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;
    //신청한 모집글 pk
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;
    //신청자
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    //모집 시 메시지 전달
    @Column
    private String message;

    //신청 상태 '수락' 시 false -> true
    @ColumnDefault("0")
    private int isAccepted;

    //지원하는 전공
    @Column
    private String applyMajor;


    // 모집 지원
    public UserApply(Post post, User user, String message, String applyMajor) {
        this.post = post;
        this.user = user;
        this.message = message;
        this.applyMajor = applyMajor;
        post.getUserApplyList().add(this);
        user.getUserApplyList().add(this);

    public void modifyAcceptedStatus(int isAccepted) {

        this.isAccepted = isAccepted;
    }

    // 모집 지원 취소
    public void cancelApply() {
        this.post.getUserApplyList().remove(this);
        this.user.getUserApplyList().remove(this);
    }
}