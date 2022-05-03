package com.hanghae99.finalprooject.repository;

import com.hanghae99.finalprooject.model.Major;
import com.hanghae99.finalprooject.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MajorRepository extends JpaRepository<Major, Long> {
    List<Major> findAllByPost(Post postId);
}