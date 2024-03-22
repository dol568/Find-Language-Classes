package com.springbootangularcourses.springbootbackend.auth;

import com.springbootangularcourses.springbootbackend.domain.User;
import com.springbootangularcourses.springbootbackend.domain.dto.ReturnUser;
import com.springbootangularcourses.springbootbackend.system.HttpResponse;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.fasterxml.jackson.databind.type.LogicalType.Map;

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
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> request = new HttpEntity<>(loginCredentials.toString(), headers);

        // Act
        ResponseEntity<HttpResponse<ReturnUser>> response = testRestTemplate.exchange("/api/login",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {
                });
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
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> request = new HttpEntity<>(loginCredentials.toString(), headers);

        // Act
        ResponseEntity<HttpResponse<ReturnUser>> response = testRestTemplate.postForEntity("/api/login",
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
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> request = new HttpEntity<>(loginCredentials.toString(), headers);

        // Act
        ResponseEntity<HttpResponse> response = testRestTemplate.exchange("/api/login",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {
                });

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
                "HTTP Status code should be 400");
        Assertions.assertEquals("Provided arguments are invalid, see data for details.", response.getBody().getMessage(),
                "Provided arguments are invalid, see data for details.");
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
        Assertions.assertEquals(userDetailsRequestJson.getString("userName"), returnUser.getUserName(),
                "Returned user's userName seems to be incorrect");
        Assertions.assertEquals(userDetailsRequestJson.getString("fullName"), returnUser.getFullName(),
                "Returned user's full name seems to be incorrect");
        Assertions.assertEquals(userDetailsRequestJson.getString("email"), returnUser.getEmail(),
                "Returned user's email seems to be incorrect");
        Assertions.assertFalse(returnUser.getId().trim().isEmpty(), "User id should not be empty");
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
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);

        // Act
        ResponseEntity<HttpResponse<ReturnUser>> response = testRestTemplate.exchange("/api/register",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {
                });

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "HTTP Status code should be 400");
        Assertions.assertEquals("User with email 'joe123@gmail.com' already exists", response.getBody().getMessage(),
                "HTTP Status code should be 400");
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
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);

        // Act
        ResponseEntity<HttpResponse<HashMap<String, String>>> response = testRestTemplate.exchange("/api/register",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {
                });

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
                "HTTP Status code should be 400");
        Assertions.assertNotNull(response.getBody());
        Assertions.assertTrue(Arrays.asList("Email is required", "Email must have between 5-125 characters", "Email needs to be a valid email").contains(response.getBody().getData().get("email")),
                "Returned user's full name seems to be incorrect");
        Assertions.assertTrue(Arrays.asList("Password is required", "Password must be at least 8 characters in length.,Password must contain at least 1 uppercase characters.,Password must contain at least 1 lowercase characters.,Password must contain at least 1 digit characters.,Password must contain at least 1 special characters.").contains(response.getBody().getData().get("password")),
                "Returned user's bio seems to be incorrect");
        Assertions.assertTrue(Arrays.asList("Full name is required", "Full name must have between 2-45 characters").contains(response.getBody().getData().get("fullName")),
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
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);

        // Act
        ResponseEntity<HttpResponse<HashMap<String, String>>> response = testRestTemplate.exchange("/api/register",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {
                });

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "HTTP Status code should be 400");
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Password must contain at least 1 special characters.", response.getBody().getData().get("password"),
                "Returned user's bio seems to be incorrect");
    }
}