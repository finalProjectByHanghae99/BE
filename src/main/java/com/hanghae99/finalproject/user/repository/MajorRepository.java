package com.hanghae99.finalproject.user.repository;

import com.hanghae99.finalproject.user.model.Major;
import com.hanghae99.finalproject.post.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MajorRepository extends JpaRepository<Major, Long> {
    List<Major> findAllByPost(Post postId);
}