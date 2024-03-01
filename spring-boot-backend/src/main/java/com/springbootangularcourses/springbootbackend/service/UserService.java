package com.springbootangularcourses.springbootbackend.service;

import com.springbootangularcourses.springbootbackend.domain.User;
import com.springbootangularcourses.springbootbackend.domain.dto.LoginDTO;
import com.springbootangularcourses.springbootbackend.domain.dto.ProfileDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    User findByEmail(String email);

    User findByUserName(String userName);

    User saveUser(User newUser);

    User updateProfile(ProfileDTO profileDTO, String userName, String email);

    void followUser(String userName, String email);

    void unfollowUser(String userName, String email);

    User authenticate(LoginDTO request);

    User getUserById(String id);

    String uploadPhoto(String id, MultipartFile file);
}
