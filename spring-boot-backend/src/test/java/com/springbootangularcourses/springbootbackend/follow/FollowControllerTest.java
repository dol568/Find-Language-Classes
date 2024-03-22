package com.springbootangularcourses.springbootbackend.follow;

import com.springbootangularcourses.springbootbackend.domain.User;
import com.springbootangularcourses.springbootbackend.domain.dto.ReturnProfile;
import com.springbootangularcourses.springbootbackend.resource.FollowController;
import com.springbootangularcourses.springbootbackend.service.UserService;
import com.springbootangularcourses.springbootbackend.system.exceptions.CustomResponseEntityExceptionHandler;
import com.springbootangularcourses.springbootbackend.utils.converter.UserToReturnProfileConverter;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = FollowController.class)
@Import(CustomResponseEntityExceptionHandler.class)
class FollowControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @MockBean
    Principal mockPrincipal;

    @MockBean
    UserToReturnProfileConverter userToReturnProfileConverter;

    String baseUrl = "/api/follow";

    ModelMapper modelMapper;

    User user;
    ReturnProfile returnProfile;

    @BeforeEach
    void setUp() {
        modelMapper = new ModelMapper();

        user = new User();
        user.setId("1");
        user.setUserName("joetalbot568");
        user.setEmail("joe@gmail.com");
        user.setFullName("Joe Talbot");
        user.setPassword("!Jjoetalbot8");
        user.setBio("im joe");

        returnProfile = modelMapper.map(user, ReturnProfile .class);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFollowSuccess() throws Exception{
        // Given
        given(mockPrincipal.getName()).willReturn("tom@gmail.com");

        given(this.userService.followUser(Mockito.any(String.class), Mockito.any(String.class)))
                .willReturn(user);
        given(this.userToReturnProfileConverter.convert(user)).willReturn(returnProfile);

        // When and then
        this.mockMvc.perform(post(this.baseUrl + "/joetalbot568")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("User has been followed"))
                .andExpect(jsonPath("$.data.username").value(user.getUserName()))
                .andExpect(jsonPath("$.data.fullName").value(user.getFullName()));
    }

    @Test
    void testFollowUserNotFound() throws Exception{
        // Given
        given(mockPrincipal.getName()).willReturn("tom@gmail.com");

        doThrow(new UsernameNotFoundException("User with username 'jonas568' not found"))
                .when(this.userService).followUser(Mockito.any(String.class), Mockito.any(String.class));

        // When and then
        this.mockMvc.perform(post(this.baseUrl + "/jonas568")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("User with username 'jonas568' not found"));
    }

    @Test
    void testUnfollowSuccess() throws Exception {
        // Given
        given(mockPrincipal.getName()).willReturn("tom@gmail.com");

        given(this.userService.unfollowUser(Mockito.any(String.class), Mockito.any(String.class)))
                .willReturn(user);
        given(this.userToReturnProfileConverter.convert(user)).willReturn(returnProfile);

        // When and then
        this.mockMvc.perform(delete(this.baseUrl + "/joetalbot568")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("User has been unfollowed"))
                .andExpect(jsonPath("$.data.username").value(user.getUserName()))
                .andExpect(jsonPath("$.data.fullName").value(user.getFullName()));
    }

    @Test
    void testUnfollowUserNotFound() throws Exception {
        // Given
        given(mockPrincipal.getName()).willReturn("tom@gmail.com");

        doThrow(new UsernameNotFoundException("User with username 'jonas568' not found"))
                .when(this.userService).unfollowUser(Mockito.any(String.class), Mockito.any(String.class));

        // When and then
        this.mockMvc.perform(delete(this.baseUrl + "/jonas568")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("User with username 'jonas568' not found"));
    }
}