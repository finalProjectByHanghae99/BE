package com.hanghae99.finalproject.sse.repository;

import com.hanghae99.finalproject.sse.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
}
