package com.hanghae99.finalproject.user.repository;

import com.hanghae99.finalproject.post.model.Post;
import com.hanghae99.finalproject.user.model.User;
import com.hanghae99.finalproject.user.model.UserRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRateRepository extends JpaRepository<UserRate,Long> {
    Optional<UserRate> findUserRateByPostAndReceiver(Post post, User user);

    //작성글 / 받는 사람 / 주는 사람
    Optional<UserRate> findUserRateByPostAndReceiverAndSender(Post post, User toUser, User User);

    void deleteAllByPostId(Long postId);
}