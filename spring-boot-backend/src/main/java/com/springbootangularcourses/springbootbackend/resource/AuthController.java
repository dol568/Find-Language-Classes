package com.springbootangularcourses.springbootbackend.resource;

import com.springbootangularcourses.springbootbackend.service.UserService;
import com.springbootangularcourses.springbootbackend.system.HttpResponse;
import com.springbootangularcourses.springbootbackend.domain.User;
import com.springbootangularcourses.springbootbackend.utils.converter.RegisterDTOToUserConverter;
import com.springbootangularcourses.springbootbackend.utils.converter.UserToReturnUserConverter;
import com.springbootangularcourses.springbootbackend.domain.dto.LoginDTO;
import com.springbootangularcourses.springbootbackend.domain.dto.RegisterDTO;
import com.springbootangularcourses.springbootbackend.domain.dto.ReturnUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final RegisterDTOToUserConverter registerDTOToUserConverter;
    private final UserToReturnUserConverter userToReturnUserConverter;

    @PostMapping("/login")
    public ResponseEntity<HttpResponse> authenticateUser(@Valid @RequestBody LoginDTO loginDTO) {

        User user = this.userService.authenticate(loginDTO);

        ReturnUser returnUser = this.userToReturnUserConverter.convert(user);

        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(returnUser)
                        .message("Successful Jwt Login")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @PostMapping("/register")
    public ResponseEntity<HttpResponse> registerUser(@Valid @RequestBody RegisterDTO registerDTO) {

        User user = this.registerDTOToUserConverter.convert(registerDTO);

        User savedUser = this.userService.saveUser(user);

        ReturnUser returnUser = this.userToReturnUserConverter.convert(savedUser);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(savedUser.getId()).toUri();

        return ResponseEntity.created(uri).body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(returnUser)
                        .message("New User Created")
                        .status(CREATED)
                        .statusCode(CREATED.value())
                        .build());
    }
}
