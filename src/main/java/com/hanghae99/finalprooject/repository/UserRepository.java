package com.hanghae99.finalprooject.repository;

import com.hanghae99.finalprooject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 이메일 중복 확인
    boolean existsByEmail(String email);

    // 닉네임 중복 확인
    boolean existsByNickname(String nickname);

    // 로그인
    Optional<User> findByEmail(String email);

    // custom service
    Optional<User> findByNickname(String nickname);

}

