package com.hanghae99.finalproject.repository;

import com.hanghae99.finalproject.model.UserPortfolioImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPortfolioImgRepository extends JpaRepository<UserPortfolioImg,Long> {

    //user pk 에 해당하는 이미지들을 list에 담아 리턴 ,
    List<UserPortfolioImg> findAllByUserId(Long userId);

}
