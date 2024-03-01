package com.springbootangularcourses.springbootbackend.repository;

import com.springbootangularcourses.springbootbackend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findByEmail(String email);
    Optional<User> findByUserName(String userName);
    Optional<User> findById(String id);
}
