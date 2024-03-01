package com.springbootangularcourses.springbootbackend.repository;

import com.springbootangularcourses.springbootbackend.domain.ChatRoomUser;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, String> {
    void deleteByUsername(String email);

    ChatRoomUser findByUsername(String name);
}
