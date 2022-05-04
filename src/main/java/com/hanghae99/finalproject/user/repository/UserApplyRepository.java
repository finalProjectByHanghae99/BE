package com.hanghae99.finalproject.user.repository;

import com.hanghae99.finalproject.user.model.User;
import com.hanghae99.finalproject.user.model.UserApply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserApplyRepository extends JpaRepository<UserApply,Long> {

    List<UserApply> findUserApplyByUser(User user);

    boolean existsByPostIdAndUserId(Long postId, Long userId);
}