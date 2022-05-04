package com.hanghae99.finalproject.repository;

import com.hanghae99.finalproject.model.Major;
import com.hanghae99.finalproject.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MajorRepository extends JpaRepository<Major, Long> {
    List<Major> findAllByPost(Post postId);
}