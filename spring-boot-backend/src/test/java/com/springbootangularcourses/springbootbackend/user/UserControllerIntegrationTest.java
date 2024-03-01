package com.springbootangularcourses.springbootbackend.user;

import com.springbootangularcourses.springbootbackend.domain.User;
import com.springbootangularcourses.springbootbackend.domain.dto.ReturnUser;
import com.springbootangularcourses.springbootbackend.system.HttpResponse;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

//@Disabled
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerIntegrationTest {

//    @Autowired
//    private TestRestTemplate testRestTemplate;
//
//    private String authorizationToken;
//
//    String baseUrl = "/api/users";
//
//    @Test
//    @DisplayName("/login works")
//    @Order(1)
//    void testSuccessfulLogin() throws Exception {
//        // Arrange
//        JSONObject loginCredentials = new JSONObject();
//        loginCredentials.put("email", "thomyorke@gmail.com");
//        loginCredentials.put("password", "123");
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//
//        HttpEntity<String> request = new HttpEntity<>(loginCredentials.toString(), headers);
//
//        // Act
//        ResponseEntity<HttpResponse<ReturnUser>> response = testRestTemplate.exchange("/api/login",
//                HttpMethod.POST,
//                request,
//                new ParameterizedTypeReference<>(){});
//        ReturnUser returnUser = response.getBody().getData();
//
//        authorizationToken = returnUser.getToken();
//
//        // Assert
//        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(),
//                "HTTP Status code should be 200");
//    }
//
//    @Test
//    @DisplayName("Check getUsers (GET)")
//    @Order(2)
//    void testGetUsersSuccess() {
//        // Arrange
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//        headers.setBearerAuth(authorizationToken);
//
//        HttpEntity request = new HttpEntity(headers);
//
//        // Act
//        ResponseEntity<HttpResponse<List<User>>> response = testRestTemplate.exchange(this.baseUrl + "/all",
//                HttpMethod.GET,
//                request,
//                new ParameterizedTypeReference<>(){});
//        List<User> users = response.getBody().getData();
//
//        // Assert
//        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(),
//                "HTTP Status code should be 200");
//        Assertions.assertTrue(users.size() == 2,
//                "Returned user's full name seems to be incorrect");
//    }
//
//    @Test
//    @DisplayName("Check getCurrentUser (GET)")
//    @Order(3)
//    void testGetCurrentUserSuccess() throws Exception {
//        // Arrange
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//        headers.setBearerAuth(authorizationToken);
//
//        HttpEntity request = new HttpEntity(headers);
//
//        // Act
//        ResponseEntity<HttpResponse<User>> response = testRestTemplate.exchange(this.baseUrl,
//                HttpMethod.GET,
//                request,
//                new ParameterizedTypeReference<>(){});
//        User user = response.getBody().getData();
//
//        // Assert
//        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(),
//                "HTTP Status code should be 200");
//        Assertions.assertEquals("thomyorke@gmail.com", user.getEmail(),
//                "Returned user's full name seems to be incorrect");
//        Assertions.assertEquals("thomy568", user.getUserName(),
//                "Returned user's full name seems to be incorrect");
//        Assertions.assertEquals("Thom Yorke", user.getFullName(),
//                "Returned user's full name seems to be incorrect");
//    }
}