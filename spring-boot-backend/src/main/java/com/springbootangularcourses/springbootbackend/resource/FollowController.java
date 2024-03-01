package com.springbootangularcourses.springbootbackend.resource;

import com.springbootangularcourses.springbootbackend.service.UserService;
import com.springbootangularcourses.springbootbackend.system.HttpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowController {

    private final UserService userService;

    @PostMapping("/{userName}")
    public ResponseEntity<HttpResponse> follow(@PathVariable String userName, Principal principal) {

        this.userService.followUser(userName, principal.getName());

        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .message("User has been followed")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @DeleteMapping("/{userName}")
    public ResponseEntity<HttpResponse> unfollow(@PathVariable String userName, Principal principal) {

        this.userService.unfollowUser(userName, principal.getName());

        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .message("User has been unfollowed")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }
}
