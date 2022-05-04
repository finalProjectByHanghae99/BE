package com.hanghae99.finalproject.img;

import com.hanghae99.finalproject.post.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface ImgRepository extends JpaRepository<Img, Long> {
    List<Img> findAllByPost(Post postId);

    List<Img> findByPostId(Long postId);

    @Transactional
    void deleteAllByPostId(Long postId);
}