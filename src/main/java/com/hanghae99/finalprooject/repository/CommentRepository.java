package com.hanghae99.finalprooject.repository;

import com.hanghae99.finalprooject.model.Comment;
import com.hanghae99.finalprooject.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPost(Post postId);
}