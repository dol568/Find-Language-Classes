package com.springbootangularcourses.springbootbackend.profile;

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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProfileControllerIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    private String authorizationToken;

    private final String baseUrl = "/api/profiles";

    @Test
    @DisplayName("/login works")
    @Order(1)
    void testSuccessfulLogin() throws Exception {
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

        authorizationToken = returnUser.getToken();

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(),
                "HTTP Status code should be 200");
    }

    @Test
    @DisplayName("Check getProfile (GET)")
    @Order(2)
    void testGetProfileSuccess() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);

        HttpEntity requestEntity = new HttpEntity(headers);

        // Act
        ResponseEntity<HttpResponse<ReturnProfile>> response = testRestTemplate.exchange(this.baseUrl + "/joshh568",
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<>(){});
        ReturnProfile returnProfile = response.getBody().getData();

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "HTTP Status code should be 200");
        Assertions.assertEquals("joshh568", returnProfile.getUsername(),
                "Returned user's username seems to be incorrect");
        Assertions.assertEquals("Josh Homme", returnProfile.getFullName(),
                "Returned user's fullname seems to be incorrect");
    }

    @Test
    @DisplayName("Check getProfile with non-existent username (GET)")
    @Order(3)
    void testGetProfileNotFound() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);

        HttpEntity requestEntity = new HttpEntity(headers);

        // Act
        ResponseEntity<HttpResponse<ReturnProfile>> response = testRestTemplate.exchange(this.baseUrl + "/jonas568",
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<>(){});

        // Assert
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "HTTP Status code should be 404");
    }

    @Test
    @DisplayName("Check updateProfile with valid input (PUT)")
    @Order(4)
    void testUpdateProfileSuccess() throws Exception {
        // Arrange
        JSONObject profileDTO = new JSONObject();
        profileDTO.put("fullName", "Jonas1234");
        profileDTO.put("bio", "hi im jonas");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);

        HttpEntity<String> request = new HttpEntity<>(profileDTO.toString(), headers);

        // Act
        ResponseEntity<HttpResponse<ReturnProfile>> response = testRestTemplate.exchange(this.baseUrl + "/thomy568",
                HttpMethod.PUT,
                request,
                new ParameterizedTypeReference<>(){});
        ReturnProfile returnProfile = response.getBody().getData();

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "HTTP Status code should be 200");
        Assertions.assertEquals("Jonas1234", returnProfile.getFullName(),
                "Returned user's full name seems to be incorrect");
        Assertions.assertEquals("hi im jonas", returnProfile.getBio(),
                "Returned user's bio seems to be incorrect");
    }

    @Test
    @DisplayName("Check updateProfile with not related current user (PUT)")
    @Order(5)
    void testUpdateProfileNotFound() throws Exception {
        // Arrange
        JSONObject profileDTO = new JSONObject();
        profileDTO.put("fullName", "Jonas1234");
        profileDTO.put("bio", "hi im jonas");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);

        HttpEntity<String> request = new HttpEntity<>(profileDTO.toString(), headers);

        // Act
        ResponseEntity<HttpResponse<ReturnProfile>> response = testRestTemplate.exchange(this.baseUrl + "/joshh568",
                HttpMethod.PUT,
                request,
                new ParameterizedTypeReference<>(){});

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
                "HTTP Status code should be 400");
    }

    @Test
    @DisplayName("Check updateProfile with invalid input (PUT)")
    @Order(6)
    void testUpdateProfileErrorWithInvalidInput() throws Exception {
        // Arrange
        JSONObject profileDTO = new JSONObject();
        profileDTO.put("fullName", "");
        profileDTO.put("bio", "");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);

        HttpEntity<String> request = new HttpEntity<>(profileDTO.toString(), headers);

        // Act
        ResponseEntity<HttpResponse<ReturnProfile>> response = testRestTemplate.exchange(this.baseUrl + "/thomy568",
                HttpMethod.PUT,
                request,
                new ParameterizedTypeReference<>(){});
        ReturnProfile returnProfile = response.getBody().getData();

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
                "HTTP Status code should be 400");
        Assertions.assertEquals("Full Name is required", returnProfile.getFullName(),
                "Returned user's full name seems to be incorrect");
        Assertions.assertEquals("Bio is required", returnProfile.getBio(),
                "Returned user's bio seems to be incorrect");
    }
}