package com.hanghae99.finalproject.repository;

import com.hanghae99.finalproject.model.Message;
import com.hanghae99.finalproject.model.Room;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
    PageImpl<Message> findByRoom(Room room, Pageable pageable);
    void deleteAllByRoom(Room room);
}
