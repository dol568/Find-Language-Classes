package com.springbootangularcourses.springbootbackend.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbootangularcourses.springbootbackend.domain.Role;
import com.springbootangularcourses.springbootbackend.domain.User;
import com.springbootangularcourses.springbootbackend.resource.AuthController;
import com.springbootangularcourses.springbootbackend.security.CustomUserDetailsService;
import com.springbootangularcourses.springbootbackend.service.UserService;
import com.springbootangularcourses.springbootbackend.system.exceptions.CustomResponseEntityExceptionHandler;
import com.springbootangularcourses.springbootbackend.system.exceptions.UsernameAlreadyExistsException;
import com.springbootangularcourses.springbootbackend.utils.converter.RegisterDTOToUserConverter;
import com.springbootangularcourses.springbootbackend.utils.converter.UserToReturnUserConverter;
import com.springbootangularcourses.springbootbackend.domain.dto.LoginDTO;
import com.springbootangularcourses.springbootbackend.domain.dto.RegisterDTO;
import com.springbootangularcourses.springbootbackend.domain.dto.ReturnUser;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = AuthController.class)
@Import(CustomResponseEntityExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AuthenticationManager authenticationManager;

    @MockBean
    UserService userService;

    @MockBean
    RegisterDTOToUserConverter registerDTOToUserConverter;

    @MockBean
    UserToReturnUserConverter userToReturnUserConverter;

    @MockBean
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    ObjectMapper objectMapper;

    ModelMapper modelMapper;

    String baseUrl = "/api";

    User user1;
    Role roleUser;
    ReturnUser returnUser1;

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

        roleUser = new Role();
        roleUser.setId(1L);
        roleUser.setName("USER");
        user1.addRole(roleUser);

        returnUser1 = modelMapper.map(user1, ReturnUser.class);
        returnUser1.setToken("Token");
        returnUser1.setPhotoUrl("");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testAuthenticateUserSuccess() throws Exception {
        // Given
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("joe@gmail.com");
        loginDTO.setPassword("!Jjoetalbot8");

        String json = this.objectMapper.writeValueAsString(loginDTO);

        given(this.userService.authenticate(Mockito.any(LoginDTO.class))).willReturn(user1);

        given(this.userToReturnUserConverter.convert(any(User.class))).willReturn(returnUser1);

        // When and then
        this.mockMvc.perform(post(this.baseUrl + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Successful Jwt Login"))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.email").value(loginDTO.getEmail()))
                .andExpect(jsonPath("$.data.userName").value(returnUser1.getUserName()))
                .andExpect(jsonPath("$.data.photoUrl").isString());
    }

    @Test
    void testAuthenticateUserNotFound() throws Exception {
        // Given
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("joe@gmail.com");
        loginDTO.setPassword("!Jjoetalbot8");

        String json = this.objectMapper.writeValueAsString(loginDTO);

        given(this.userService.authenticate(Mockito.any(LoginDTO.class)))
                .willThrow(new BadCredentialsException("Bad credentials"));

        // When and then
        this.mockMvc.perform(post(this.baseUrl + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.statusCode").value(401))
                .andExpect(jsonPath("$.message").value("Bad credentials"));
    }

    @Test
    void testRegisterUserSuccess() throws Exception {
        // Given
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUserName("joetalbot123");
        registerDTO.setEmail("joe123@gmail.com");
        registerDTO.setFullName("Joe Talbot");
        registerDTO.setPassword("!Jjoetalbot8");

        User regUser = modelMapper.map(registerDTO, User.class);

        User savedUser = modelMapper.map(regUser, User.class);
        savedUser.setId("1");

        ReturnUser returnUser = modelMapper.map(savedUser, ReturnUser.class);
        returnUser.setToken("Token");
        returnUser.setPhotoUrl("");

        String json = this.objectMapper.writeValueAsString(registerDTO);

        given(this.registerDTOToUserConverter.convert(Mockito.any(RegisterDTO.class))).willReturn(regUser);

        given(this.userService.saveUser(Mockito.any(User.class))).willReturn(savedUser);

        given(this.userToReturnUserConverter.convert(Mockito.any(User.class))).willReturn(returnUser);

        this.mockMvc.perform(post(this.baseUrl + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.message").value("New User Created"))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.email").value(registerDTO.getEmail()))
                .andExpect(jsonPath("$.data.userName").value(registerDTO.getUserName()))
                .andExpect(jsonPath("$.data.photoUrl").exists());
    }

    @Test
    void testRegisterUserEmailAlreadyExists() throws Exception {
        // Given
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUserName("joetalbot123");
        registerDTO.setEmail("joe123@gmail.com");
        registerDTO.setFullName("Joe Talbot");
        registerDTO.setPassword("!Jjoetalbot8");

        User regUser = modelMapper.map(registerDTO, User.class);

        String json = this.objectMapper.writeValueAsString(registerDTO);

        given(this.registerDTOToUserConverter.convert(Mockito.any(RegisterDTO.class))).willReturn(regUser);

        given(this.userService.saveUser(Mockito.any(User.class)))
                .willThrow(new UsernameAlreadyExistsException("User with email 'joe123@gmail.com' already exists"));

        this.mockMvc.perform(post(this.baseUrl + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value("User with email 'joe123@gmail.com' already exists"));
    }
}