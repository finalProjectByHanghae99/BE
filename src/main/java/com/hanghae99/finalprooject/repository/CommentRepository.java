package com.hanghae99.finalprooject.repository;

import com.hanghae99.finalprooject.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}