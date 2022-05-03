package com.hanghae99.finalproject.repository;


import com.hanghae99.finalproject.model.User;
import com.hanghae99.finalproject.model.UserApply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserApplyRepository extends JpaRepository<UserApply,Long> {


    //userPk를 이용해서 UserApply 객체를 찾아온다

    List<UserApply> findUserApplyByUser(User user);



}
