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

    User getUserById(String id);

    User saveUser(User newUser);

    User updateProfile(ProfileDTO profileDTO, String userName, String email);

    User authenticate(LoginDTO request);

    User followUser(String userName, String email);

    User unfollowUser(String userName, String email);

    String uploadPhoto(String id, MultipartFile file);
}
