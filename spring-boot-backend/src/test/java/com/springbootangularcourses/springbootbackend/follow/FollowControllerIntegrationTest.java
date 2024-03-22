package com.springbootangularcourses.springbootbackend.follow;

import com.springbootangularcourses.springbootbackend.domain.dto.ReturnProfile;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FollowControllerIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    private String authorizationToken;

    private final String baseUrl = "/api/follow";

    @Test
    @DisplayName("User can be created")
    @Order(1)
    void testCreateUser_whenValidDetailsProvided_returnsUserDetails() throws Exception {
        // Arrange
        JSONObject userDetailsRequestJson = new JSONObject();
        userDetailsRequestJson.put("userName", "joetalbot12345");
        userDetailsRequestJson.put("fullName", "Joe Talbot45");
        userDetailsRequestJson.put("email", "joe12345@gmail.com");
        userDetailsRequestJson.put("password", "!Jjoetalbot8");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);

        // Act
        ResponseEntity<HttpResponse<ReturnUser>> response = testRestTemplate.exchange("/api/register",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {
                });
        ReturnUser returnUser = response.getBody().getData();

        // Assert
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertFalse(returnUser.getId().trim().isEmpty(), "User id should not be empty");
    }

    @Test
    @DisplayName("/login works")
    @Order(2)
    void testSuccessfulLogin() throws Exception {
        // Arrange
        JSONObject loginCredentials = new JSONObject();
        loginCredentials.put("email", "joe12345@gmail.com");
        loginCredentials.put("password", "!Jjoetalbot8");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> request = new HttpEntity<>(loginCredentials.toString(), headers);

        // Act
        ResponseEntity<HttpResponse<ReturnUser>> response = testRestTemplate.exchange("/api/login",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {
                });
        ReturnUser returnUser = response.getBody().getData();

        authorizationToken = returnUser.getToken();

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(),
                "HTTP Status code should be 200");
        Assertions.assertNotNull(authorizationToken,
                "Response should contain Authorization header with JWT");
        Assertions.assertNotNull(returnUser.getId().trim().isEmpty(),
                "Response should contain UserID in a response header");
    }

    @Test
    @DisplayName("Check follow valid user (POST)")
    @Order(3)
    void testFollowSuccess() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // Act
        ResponseEntity<HttpResponse<ReturnProfile>> response = testRestTemplate.exchange(this.baseUrl + "/thomy568",
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<>() {
                });
        ReturnProfile returnProfile = response.getBody().getData();

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "HTTP Status code should be 200");
        Assertions.assertEquals(1, returnProfile.getFollowers().size(),
                "Returned profile's number of followers to be incorrect");
    }

    @Test
    @DisplayName("Check follow non-existent user (POST)")
    @Order(4)
    void testFollowErrorUserNotFound() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // Act
        ResponseEntity<HttpResponse<ReturnProfile>> response = testRestTemplate.exchange(this.baseUrl + "/jonas568",
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<>() {
                });

        // Assert
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(),
                "HTTP Status code should be 404");
    }

    @Test
    @DisplayName("Check follow yourself (POST)")
    @Order(5)
    void testFollowErrorYourself() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // Act
        ResponseEntity<HttpResponse<ReturnProfile>> response = testRestTemplate.exchange(this.baseUrl + "/joetalbot12345",
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<>() {
                });

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
                "HTTP Status code should be 400");
    }

    @Test
    @DisplayName("Check unfollow valid user (DELETE)")
    @Order(6)
    void testUnfollowSuccess() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // Act
        ResponseEntity<HttpResponse<ReturnProfile>> response = testRestTemplate.exchange(this.baseUrl + "/thomy568",
                HttpMethod.DELETE,
                requestEntity,
                new ParameterizedTypeReference<>() {
                });
        ReturnProfile returnProfile = response.getBody().getData();

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "HTTP Status code should be 200");
        Assertions.assertEquals(0, returnProfile.getFollowers().size(),
                "Returned profile's number of followers to be incorrect");
    }

    @Test
    @DisplayName("Check unfollow non-existent user (DELETE)")
    @Order(7)
    void testUnfollowErrorUserNotFound() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // Act
        ResponseEntity<HttpResponse<ReturnProfile>> response = testRestTemplate.exchange(this.baseUrl + "/jonas568",
                HttpMethod.DELETE,
                requestEntity,
                new ParameterizedTypeReference<>() {
                });

        // Assert
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "HTTP Status code should be 404");
    }

    @Test
    @DisplayName("Check unfollow not followed user (DELETE)")
    @Order(8)
    void testUnfollowErrorNotFollowed() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // Act
        ResponseEntity<HttpResponse<ReturnProfile>> response = testRestTemplate.exchange(this.baseUrl + "/thomy568",
                HttpMethod.DELETE,
                requestEntity,
                new ParameterizedTypeReference<>() {
                });

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
                "HTTP Status code should be 400");
    }

    @Test
    @DisplayName("Check unfollow yourself (DELETE)")
    @Order(9)
    void testUnfollowErrorYourself() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // Act
        ResponseEntity<HttpResponse<ReturnProfile>> response = testRestTemplate.exchange(this.baseUrl + "/joetalbot12345",
                HttpMethod.DELETE,
                requestEntity,
                new ParameterizedTypeReference<>() {
                });

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
                "HTTP Status code should be 400");
    }
}
