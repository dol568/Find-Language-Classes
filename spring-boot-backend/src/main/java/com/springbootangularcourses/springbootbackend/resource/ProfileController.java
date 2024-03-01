package com.springbootangularcourses.springbootbackend.resource;

import com.springbootangularcourses.springbootbackend.service.UserService;
import com.springbootangularcourses.springbootbackend.system.HttpResponse;
import com.springbootangularcourses.springbootbackend.domain.User;
import com.springbootangularcourses.springbootbackend.utils.converter.UserToReturnProfileConverter;
import com.springbootangularcourses.springbootbackend.domain.dto.ProfileDTO;
import com.springbootangularcourses.springbootbackend.domain.dto.ReturnProfile;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDateTime;

import static com.springbootangularcourses.springbootbackend.system.Constant.PHOTO_DIRECTORY;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final UserToReturnProfileConverter userToReturnProfileConverter;

    @GetMapping("/{userName}")
    public ResponseEntity<HttpResponse> getProfile(@PathVariable String userName) {

        User user = this.userService.findByUserName(userName);

        ReturnProfile returnProfile = this.userToReturnProfileConverter.convert(user);

        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(returnProfile)
                        .message("Profile retrieved")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @PutMapping("/{userName}")
    public ResponseEntity<HttpResponse> updateProfile(@Valid @RequestBody ProfileDTO profileDTO,
                                                      @PathVariable String userName, Principal principal) {

        User user = this.userService.updateProfile(profileDTO, userName, principal.getName());

        ReturnProfile returnProfile = this.userToReturnProfileConverter.convert(user);

        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(returnProfile)
                        .message("Profile updated")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @PutMapping("/photo")
    public ResponseEntity<HttpResponse> uploadPhoto(@RequestParam("id") String id, @RequestParam("file")MultipartFile file) {

        String photoUrl = this.userService.uploadPhoto(id, file);

        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(photoUrl)
                        .message("Photo uploaded")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @GetMapping(path = "/image/{filename}", produces = {IMAGE_PNG_VALUE, IMAGE_JPEG_VALUE})
    public byte[] getPhoto(@PathVariable("filename") String filename) throws IOException {
        return Files.readAllBytes(Paths.get(PHOTO_DIRECTORY + filename));
    }
}
