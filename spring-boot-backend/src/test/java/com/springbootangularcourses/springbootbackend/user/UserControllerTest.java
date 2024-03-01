package com.springbootangularcourses.springbootbackend.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbootangularcourses.springbootbackend.domain.Role;
import com.springbootangularcourses.springbootbackend.domain.User;
import com.springbootangularcourses.springbootbackend.resource.UserController;
import com.springbootangularcourses.springbootbackend.service.UserService;
import com.springbootangularcourses.springbootbackend.system.exceptions.CustomResponseEntityExceptionHandler;
import com.springbootangularcourses.springbootbackend.utils.converter.RegisterDTOToUserConverter;
import com.springbootangularcourses.springbootbackend.utils.converter.UserToReturnUserConverter;
import com.springbootangularcourses.springbootbackend.domain.dto.ReturnUser;
import org.hamcrest.Matchers;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = UserController.class)
@Import(CustomResponseEntityExceptionHandler.class)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    Principal mockPrincipal;

    @MockBean
    AuthenticationManager authenticationManager;

    @MockBean
    UserService userService;

    @MockBean
    RegisterDTOToUserConverter registerDTOToUserConverter;

    @MockBean
    UserToReturnUserConverter userToReturnUserConverter;

    @Autowired
    ObjectMapper objectMapper;

    ModelMapper modelMapper;

    String baseUrl = "/api/users";

    User user1;
    User user2;
    Role roleUser;
    List<User> users;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId("1");
        user1.setUserName("joetalbot568");
        user1.setEmail("joe@gmail.com");
        user1.setFullName("Joe Talbot");
        user1.setPassword("!Jjoetalbot8");
        user1.setBio("im joe");

        user2 = new User();
        user2.setId("2");
        user2.setUserName("thomyorke123");
        user2.setEmail("tom@gmail.com");
        user2.setFullName("Thom Yorke");
        user2.setPassword("!Jthomyork8");
        user2.setBio("im tom");

        roleUser = new Role();
        roleUser.setId(1L);
        roleUser.setName("USER");

        user1.addRole(roleUser);
        user2.addRole(roleUser);

        users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        modelMapper = new ModelMapper();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testGetUsersSuccess() throws Exception {
        // Given
        given(this.userService.getAllUsers()).willReturn(this.users);

        // When and then
        this.mockMvc.perform(get(this.baseUrl + "/all")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Users retrieved"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(this.users.size())))
                .andExpect(jsonPath("$.data[0].id").value(1L))
                .andExpect(jsonPath("$.data[0].userName").value("joetalbot568"))
                .andExpect(jsonPath("$.data[1].id").value(2L))
                .andExpect(jsonPath("$.data[1].userName").value("thomyorke123"));
    }

    @Test
    void testGetCurrentUserSuccess() throws Exception {
        // Given
        ReturnUser returnUser = modelMapper.map(user1, ReturnUser.class);
        returnUser.setToken("Token");
        returnUser.setPhotoUrl("");

        given(mockPrincipal.getName()).willReturn("tom@gmail.com");

        given(this.userService.findByEmail(Mockito.any(String.class)))
                .willReturn(user1);

        given(this.userToReturnUserConverter.convert(Mockito.any(User.class)))
                .willReturn(returnUser);

        // When and then
        this.mockMvc.perform(get(this.baseUrl)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("User retrieved"))
                .andExpect(jsonPath("$.data.email").value("joe@gmail.com"))
                .andExpect(jsonPath("$.data.fullName").value("Joe Talbot"))
                .andExpect(jsonPath("$.data.userName").value("joetalbot568"))
                .andExpect(jsonPath("$.data.token").isNotEmpty());
    }
}