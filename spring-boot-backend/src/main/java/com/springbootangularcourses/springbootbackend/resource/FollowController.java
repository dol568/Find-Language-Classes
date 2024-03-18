package com.springbootangularcourses.springbootbackend.resource;

import com.springbootangularcourses.springbootbackend.domain.User;
import com.springbootangularcourses.springbootbackend.domain.dto.ReturnProfile;
import com.springbootangularcourses.springbootbackend.service.UserService;
import com.springbootangularcourses.springbootbackend.system.HttpResponse;
import com.springbootangularcourses.springbootbackend.utils.converter.UserToReturnProfileConverter;
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
    private final UserToReturnProfileConverter userToReturnProfileConverter;

    @PostMapping("/{userName}")
    public ResponseEntity<HttpResponse> follow(@PathVariable String userName, Principal principal) {

        User user = this.userService.followUser(userName, principal.getName());

        ReturnProfile returnProfile = this.userToReturnProfileConverter.convert(user);

        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(returnProfile)
                        .message("User has been followed")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @DeleteMapping("/{userName}")
    public ResponseEntity<HttpResponse> unfollow(@PathVariable String userName, Principal principal) {

        User user = this.userService.unfollowUser(userName, principal.getName());

        ReturnProfile returnProfile = this.userToReturnProfileConverter.convert(user);

        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(returnProfile)
                        .message("User has been unfollowed")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }
}
