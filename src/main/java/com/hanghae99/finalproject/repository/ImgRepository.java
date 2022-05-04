package com.hanghae99.finalproject.repository;

import com.hanghae99.finalproject.model.Img;
import com.hanghae99.finalproject.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface ImgRepository extends JpaRepository<Img, Long> {
    List<Img> findAllByPost(Post postId);

    List<Img> findByPostId(Long postId);

    @Transactional
    void deleteAllByPostId(Long postId);
}