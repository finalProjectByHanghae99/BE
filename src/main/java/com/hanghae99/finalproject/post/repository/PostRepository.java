package com.hanghae99.finalproject.post.repository;

import com.hanghae99.finalproject.post.model.Post;
import com.hanghae99.finalproject.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByCreatedAtDesc();
    List<Post> findPostsByUser(User user);



}