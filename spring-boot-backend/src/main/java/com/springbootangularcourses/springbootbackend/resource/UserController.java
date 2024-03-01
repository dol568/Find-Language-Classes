package com.springbootangularcourses.springbootbackend.resource;

import com.springbootangularcourses.springbootbackend.service.UserService;
import com.springbootangularcourses.springbootbackend.system.HttpResponse;
import com.springbootangularcourses.springbootbackend.domain.User;
import com.springbootangularcourses.springbootbackend.utils.converter.UserToReturnUserConverter;
import com.springbootangularcourses.springbootbackend.domain.dto.ReturnUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserToReturnUserConverter userToReturnUserConverter;

    @GetMapping("/all")
    public ResponseEntity<HttpResponse> getUsers() {
        List<User> users = this.userService.getAllUsers();

        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(users)
                        .message("Users retrieved")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @GetMapping("")
    public ResponseEntity<HttpResponse> getCurrentUser(Principal principal) {

        User user = this.userService.findByEmail(principal.getName());

        ReturnUser returnUser = this.userToReturnUserConverter.convert(user);

        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(returnUser)
                        .message("User retrieved")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }
}
