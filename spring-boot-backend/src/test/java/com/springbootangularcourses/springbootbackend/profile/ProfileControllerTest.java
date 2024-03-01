package com.springbootangularcourses.springbootbackend.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbootangularcourses.springbootbackend.domain.User;
import com.springbootangularcourses.springbootbackend.resource.ProfileController;
import com.springbootangularcourses.springbootbackend.service.UserService;
import com.springbootangularcourses.springbootbackend.system.exceptions.CustomResponseEntityExceptionHandler;
import com.springbootangularcourses.springbootbackend.utils.converter.UserToReturnProfileConverter;
import com.springbootangularcourses.springbootbackend.domain.dto.ProfileDTO;
import com.springbootangularcourses.springbootbackend.domain.dto.ReturnProfile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = ProfileController.class)
@Import(CustomResponseEntityExceptionHandler.class)
class ProfileControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @MockBean
    UserToReturnProfileConverter userToReturnProfileConverter;

    @MockBean
    Principal mockPrincipal;

    @Autowired
    ObjectMapper objectMapper;

    ModelMapper modelMapper;

    String baseUrl = "/api/profiles";

    User user1;
    User user2;
    ReturnProfile rp1;
    ReturnProfile rp2;

    @BeforeEach
    void setUp() {
        modelMapper = new ModelMapper();

        user1 = new User();
        user1.setId("1");
        user1.setUserName("joetalbot568");
        user1.setEmail("joe@gmail.com");
        user1.setFullName("Joe Talbot");
        user1.setPassword("!Jjoetalbot8");
        user1.setBio("im joe");
        user1.setPhotoUrl("");

        rp1 = modelMapper.map(user1, ReturnProfile.class);

        user2 = new User();
        user2.setId("2");
        user2.setUserName("thomyorke123");
        user2.setEmail("tom@gmail.com");
        user2.setFullName("Thom Yorke");
        user2.setPassword("!Jthomyork8");
        user2.setBio("im tom");
        user2.setPhotoUrl("");

        rp2 = modelMapper.map(user2, ReturnProfile.class);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testGetProfileSuccess() throws Exception {
        // Given
        given(this.userService.findByUserName("joetalbot568")).willReturn(user1);
        given(this.userToReturnProfileConverter.convert(user1)).willReturn(rp1);

        // When and then
        this.mockMvc.perform(get(this.baseUrl + "/joetalbot568")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Profile retrieved"))
                .andExpect(jsonPath("$.data.username").value(user1.getUserName()))
                .andExpect(jsonPath("$.data.fullName").value(user1.getFullName()));
    }

    @Test
    void testGetProfileNotFound() throws Exception {
        // Given
        given(this.userService.findByUserName("joetalbot568"))
                .willThrow(new UsernameNotFoundException("User with username 'joetalbot568' not found"));

        // When and then
        this.mockMvc.perform(get(this.baseUrl + "/joetalbot568")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("User with username 'joetalbot568' not found"));
    }

    @Test
    void testUpdateProfileSuccess() throws Exception {
        // Given
        ProfileDTO profileDTO = new ProfileDTO("Jonas1234", "hi im jonas");

        String json = this.objectMapper.writeValueAsString(profileDTO);

        User updated = new User();
        updated.setId("2");
        updated.setUserName("thomyorke123");
        updated.setEmail("tom@gmail.com");
        updated.setFullName("Jonas1234");
        updated.setPassword("!Jthomyork8");
        updated.setBio("hi im jonas");
        updated.setPhotoUrl("");

        ReturnProfile rpUpdated = modelMapper.map(updated, ReturnProfile.class);

        given(mockPrincipal.getName()).willReturn("tom@gmail.com");

        given(this.userService
                .updateProfile(Mockito.any(ProfileDTO.class), Mockito.any(String.class), Mockito.any(String.class)))
                .willReturn(updated);

        given(this.userToReturnProfileConverter.convert(Mockito.any(User.class)))
                .willReturn(rpUpdated);

        // When and then
        this.mockMvc.perform(put(this.baseUrl + "/thomyorke123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Profile updated"))
                .andExpect(jsonPath("$.data.bio").value(profileDTO.getBio()))
                .andExpect(jsonPath("$.data.fullName").value(profileDTO.getFullName()));
    }

    @Test
    void testUpdateProfileNotFound() throws Exception {
        // Given
        ProfileDTO profileDTO = new ProfileDTO("Jonas1234", "hi im jonas");

        String json = this.objectMapper.writeValueAsString(profileDTO);

        given(mockPrincipal.getName()).willReturn("tom@gmail.com");

        given(this.userService
                .updateProfile(Mockito.any(ProfileDTO.class), Mockito.any(String.class), Mockito.any(String.class)))
                .willThrow(new UsernameNotFoundException("User with username 'thomyorke123' not found"));

        // When and then
        this.mockMvc.perform(put(this.baseUrl + "/thomyorke123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("User with username 'thomyorke123' not found"));
    }
}