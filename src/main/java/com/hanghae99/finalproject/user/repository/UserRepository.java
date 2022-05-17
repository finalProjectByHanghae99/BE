package com.hanghae99.finalproject.user.repository;

import com.hanghae99.finalproject.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 이메일 중복 확인
    boolean existsByMemberId(String memberId);

    // 닉네임 중복 확인
    boolean existsByNickname(String nickname);

    // 로그인
    Optional<User> findByMemberId(String memberId);

    Optional<User> findByNickname(String nickname);
}