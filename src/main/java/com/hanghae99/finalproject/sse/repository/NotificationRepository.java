package com.hanghae99.finalproject.sse.repository;

import com.hanghae99.finalproject.sse.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
    @Query("select n from Notification n " +
            "where n.receiver.id = :userId " +
            "order by n.id desc")
    List<Notification> findAllByUserId(@Param("userId") Long userId);
}
