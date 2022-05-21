package com.hanghae99.finalproject.user.model;


import com.hanghae99.finalproject.post.model.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
@Entity
public class UserRate {

    // 모집글 -> 스테이터스가 마감 상태일 때
    // 인원에게 평점을 매길 수 있다 .-> 좋아요 .
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column
    private int ratePoint;

    @Column
    private Boolean rateStatus;

    //평점을 받는다면 status 상태가 true :)
    public void updateStatus(boolean status){
        this.rateStatus = status;
    }

}
