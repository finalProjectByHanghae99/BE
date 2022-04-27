package com.hanghae99.finalprooject.repository;

import com.hanghae99.finalprooject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
