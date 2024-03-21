package com.springbootangularcourses.springbootbackend.auth;

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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthControllerIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    @DisplayName("Check authenticateUser (POST)")
    @Order(1)
    void testAuthenticateUserSuccess() throws Exception {
        // Arrange
        JSONObject loginCredentials = new JSONObject();
        loginCredentials.put("email", "thomyorke@gmail.com");
        loginCredentials.put("password", "123");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<String> request = new HttpEntity<>(loginCredentials.toString(), headers);

        // Act
        ResponseEntity<HttpResponse<ReturnUser>> response = testRestTemplate.exchange("/api/login",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>(){});
        ReturnUser returnUser = response.getBody().getData();

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(),
                "HTTP Status code should be 200");
        Assertions.assertNotNull(returnUser.getToken(),
                "Response should contain Authorization header with JWT");
        Assertions.assertNotNull(returnUser.getId().trim().isEmpty(),
                "Response should contain UserID in a response header");
    }

    @Test
    @DisplayName("Check authenticateUser bad credentials (POST)")
    @Order(2)
    void testAuthenticateUserErrorBadCredentials() throws Exception {
        // Arrange
        JSONObject loginCredentials = new JSONObject();
        loginCredentials.put("email", "thomyorke@gmail.com");
        loginCredentials.put("password", "568");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<String> request = new HttpEntity<>(loginCredentials.toString(), headers);

        // Act
        ResponseEntity response = testRestTemplate.postForEntity("/api/login",
                request,
                null);

        // Assert
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode(),
                "HTTP Status code should be 401");
    }

    @Test
    @DisplayName("Check authenticateUser with invalid input (POST)")
    @Order(3)
    void testAuthenticateUserErrorWithInvalidInput() throws Exception {
        // Arrange
        JSONObject loginCredentials = new JSONObject();
        loginCredentials.put("email", "");
        loginCredentials.put("password", "");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<String> request = new HttpEntity<>(loginCredentials.toString(), headers);

        // Act
        ResponseEntity<HttpResponse<User>> response = testRestTemplate.exchange("/api/login",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>(){});
        User user = response.getBody().getData();

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
                "HTTP Status code should be 400");
        Assertions.assertEquals("Email is required", user.getEmail(),
                "Returned user's full name seems to be incorrect");
        Assertions.assertEquals("Password is required", user.getPassword(),
                "Returned user's bio seems to be incorrect");
    }

    @Test
    @DisplayName("Check registerUser (POST)")
    @Order(4)
    void testRegisterUserSuccess() throws Exception {
        // Arrange
        JSONObject userDetailsRequestJson = new JSONObject();
        userDetailsRequestJson.put("userName", "joetalbot123");
        userDetailsRequestJson.put("fullName", "Joe Talbot");
        userDetailsRequestJson.put("email", "joe123@gmail.com");
        userDetailsRequestJson.put("password", "!Jjoetalbot8");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);

        // Act
        ResponseEntity<HttpResponse<User>> response = testRestTemplate.exchange("/api/register",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>(){});
        User user = response.getBody().getData();

        // Assert
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals(userDetailsRequestJson.getString("userName"), user.getUserName(),
                "Returned user's userName seems to be incorrect");
        Assertions.assertEquals(userDetailsRequestJson.getString("fullName"), user.getFullName(),
                "Returned user's full name seems to be incorrect");
        Assertions.assertEquals(userDetailsRequestJson.getString("email"), user.getEmail(),
                "Returned user's email seems to be incorrect");
        Assertions.assertFalse(user.getId().trim().isEmpty(), "User id should not be empty");
    }

    @Test
    @DisplayName("Check registerUser duplicate email (POST)")
    @Order(5)
    void testRegisterUserErrorDuplicateEmail() throws Exception {
        // Arrange
        JSONObject userDetailsRequestJson = new JSONObject();
        userDetailsRequestJson.put("userName", "joetalbot123");
        userDetailsRequestJson.put("fullName", "Joe Talbot");
        userDetailsRequestJson.put("email", "joe123@gmail.com");
        userDetailsRequestJson.put("password", "!Jjoetalbot8");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);

        // Act
        ResponseEntity<HttpResponse<User>> response = testRestTemplate.exchange("/api/register",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>(){});
        User user = response.getBody().getData();

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Check registerUser with invalid input (POST)")
    @Order(6)
    void testRegisterUserErrorWithInvalidInput() throws Exception {
        // Arrange
        JSONObject userDetailsRequestJson = new JSONObject();
        userDetailsRequestJson.put("userName", "");
        userDetailsRequestJson.put("fullName", "");
        userDetailsRequestJson.put("email", "");
        userDetailsRequestJson.put("password", "");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);

        // Act
        ResponseEntity<HttpResponse<User>> response = testRestTemplate.exchange("/api/register",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>(){});
        User user = response.getBody().getData();

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
                "HTTP Status code should be 400");
        Assertions.assertTrue(Arrays.asList("Email is required", "Email must have between 5-125 characters", "Email needs to be a valid email").contains(user.getEmail()),
                "Returned user's full name seems to be incorrect");
        Assertions.assertTrue(Arrays.asList("Password is required", "Password must be at least 8 characters in length.,Password must contain at least 1 uppercase characters.,Password must contain at least 1 lowercase characters.,Password must contain at least 1 digit characters.,Password must contain at least 1 special characters.").contains(user.getPassword()),
                "Returned user's bio seems to be incorrect");
        Assertions.assertTrue(Arrays.asList("Full name is required", "Full name must have between 2-45 characters").contains(user.getFullName()),
                "Returned user's bio seems to be incorrect");
    }

    @Test
    @DisplayName("Check registerUser with password no special char (POST)")
    @Order(7)
    void testRegisterUserErrorWithInvalidInputPasswordNoChar() throws Exception {
        // Arrange
        JSONObject userDetailsRequestJson = new JSONObject();
        userDetailsRequestJson.put("userName", "joetalbot123");
        userDetailsRequestJson.put("fullName", "Joe Talbot");
        userDetailsRequestJson.put("email", "joe123@gmail.com");
        userDetailsRequestJson.put("password", "Jjoetalbot8");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);

        // Act
        ResponseEntity<HttpResponse<User>> response = testRestTemplate.exchange("/api/register",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>(){});
        User user = response.getBody().getData();

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Password must contain at least 1 special characters.", user.getPassword(),
                "Returned user's bio seems to be incorrect");
    }
}