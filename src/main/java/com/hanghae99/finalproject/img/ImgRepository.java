package com.hanghae99.finalproject.img;

import com.hanghae99.finalproject.post.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImgRepository extends JpaRepository<Img, Long> {
    List<Img> findAllByPost(Post postId);
}