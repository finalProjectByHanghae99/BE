package com.hanghae99.finalproject.chatRoom.repository;

import com.hanghae99.finalproject.chatRoom.model.Room;
import com.hanghae99.finalproject.user.model.User;
import com.hanghae99.finalproject.chatRoom.model.UserRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoomRepository extends JpaRepository<UserRoom,Long> {

    UserRoom findByRoomAndUser(Room room, User user);
    UserRoom findByRoomAndUserAndToUser(Room room, User user,User toUser);
    List<UserRoom> findByUser(User user);
    List<UserRoom> findByRoom(Room room);




}
