package com.hanghae99.finalproject.comment.repository;

import com.hanghae99.finalproject.comment.model.Comment;
import com.hanghae99.finalproject.post.model.Post;
import com.hanghae99.finalproject.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPost(Post postId);

    List<Comment> findAllByPostAndUser(Post post, User user);
}