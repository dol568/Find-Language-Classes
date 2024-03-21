package com.springbootangularcourses.springbootbackend.language_class;

import com.springbootangularcourses.springbootbackend.domain.LanguageClass;
import com.springbootangularcourses.springbootbackend.domain.dto.ReturnLanguageClass;
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
class LanguageClassControllerIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    private String authorizationToken;

    private final String baseUrl = "/api/languageClasses";

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
                new ParameterizedTypeReference<>() {
                });
        ReturnUser returnUser = response.getBody().getData();

        authorizationToken = returnUser.getToken();

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(),
                "HTTP Status code should be 200");
    }

    @Test
    @DisplayName("Check addLanguageClass with valid input (POST)")
    @Order(2)
    void testAddLanguageClassSuccess() throws Exception {
        // Arrange
        JSONObject newLanguageClass = new JSONObject();
        newLanguageClass.put("title", "Beginner French Class");
        newLanguageClass.put("time", "11:00");
        newLanguageClass.put("description", "Class for beginners");
        newLanguageClass.put("category", "French");
        newLanguageClass.put("dayOfWeek", 1);
        newLanguageClass.put("city", "Warsaw");
        newLanguageClass.put("address", "271 Street");
        newLanguageClass.put("postalCode", "V1 712L");
        newLanguageClass.put("province", "MB");
        newLanguageClass.put("country", "Poland");
        newLanguageClass.put("totalSpots", 10);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);

        HttpEntity<String> request = new HttpEntity<>(newLanguageClass.toString(), headers);

        // Act
        ResponseEntity<HttpResponse<ReturnLanguageClass>> response = testRestTemplate.exchange(this.baseUrl,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>(){});

        ReturnLanguageClass returnLanguageClass = response.getBody().getData();

        // Assert
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode(),
                "HTTP Status code should be 200");
        Assertions.assertNotNull(returnLanguageClass.getId(),
                "Response should contain UserID in a response header");
        Assertions.assertEquals(newLanguageClass.get("title"), returnLanguageClass.getTitle(),
                "Size of retrieved Language classes seems to be incorrect");
        Assertions.assertEquals(newLanguageClass.get("description"), returnLanguageClass.getDescription(),
                "Size of retrieved Language classes seems to be incorrect");
        Assertions.assertEquals(newLanguageClass.get("totalSpots"), returnLanguageClass.getTotalSpots(),
                "Size of retrieved Language classes seems to be incorrect");
    }

    @Test
    @DisplayName("Check addLanguageClass with invalid input (POST)")
    @Order(3)
    void testAddLanguageClassErrorWithInvalidInput() throws Exception {
        // Arrange
        JSONObject newLanguageClass = new JSONObject();
        newLanguageClass.put("title", "");
        newLanguageClass.put("time", "");
        newLanguageClass.put("description", "");
        newLanguageClass.put("category", "French");
        newLanguageClass.put("dayOfWeek", 1);
        newLanguageClass.put("city", "Warsaw");
        newLanguageClass.put("address", "271 Street");
        newLanguageClass.put("postalCode", "V1 712L");
        newLanguageClass.put("province", "MB");
        newLanguageClass.put("country", "Poland");
        newLanguageClass.put("totalSpots", 10);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);

        HttpEntity<String> request = new HttpEntity<>(newLanguageClass.toString(), headers);

        // Act
        ResponseEntity<HttpResponse<ReturnLanguageClass>> response = testRestTemplate.exchange(this.baseUrl,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>(){});

        ReturnLanguageClass returnLanguageClass = response.getBody().getData();

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
                "HTTP Status code should be 400");
        Assertions.assertEquals("Title is required", returnLanguageClass.getTitle(),
                "Returned user's full name seems to be incorrect");
        Assertions.assertEquals("Description is required", returnLanguageClass.getDescription(),
                "Returned user's full name seems to be incorrect");
        Assertions.assertEquals("Time is required", returnLanguageClass.getTime(),
                "Returned user's full name seems to be incorrect");
    }

    @Test
    @DisplayName("Check getLanguageClasses (GET)")
    @Order(4)
    void testGetLanguageClassesSuccess() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);

        HttpEntity request = new HttpEntity(headers);

        // Act
        ResponseEntity<HttpResponse<List<LanguageClass>>> response = testRestTemplate.exchange(this.baseUrl,
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {
                });
        List<LanguageClass> languageClasses = response.getBody().getData();

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(),
                "HTTP Status code should be 200");
        Assertions.assertTrue(languageClasses.size() == 3,
                "Size of retrieved Language classes seems to be incorrect");
    }

    @Test
    @DisplayName("Check getLanguageClass (GET)")
    @Order(5)
    void testGetLanguageClassSuccess() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);

        HttpEntity request = new HttpEntity(headers);

        // Act
        ResponseEntity<HttpResponse<LanguageClass>> response = testRestTemplate.exchange(this.baseUrl + "/3",
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {
                });
        LanguageClass languageClasses = response.getBody().getData();

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(),
                "HTTP Status code should be 200");
        Assertions.assertEquals(3, languageClasses.getId(),
                "Size of retrieved Language classes seems to be incorrect");
        Assertions.assertEquals("Beginner French Class", languageClasses.getTitle(),
                "Size of retrieved Language classes seems to be incorrect");
        Assertions.assertEquals(1, languageClasses.getUserLanguageClasses().size(),
                "Size of retrieved Language classes seems to be incorrect");
    }

    @Test
    @DisplayName("Check getLanguageClass with non-existent id (GET)")
    @Order(6)
    void testGetLanguageClassErrorIdNotFound() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);

        HttpEntity request = new HttpEntity(headers);

        // Act
        ResponseEntity<HttpResponse<LanguageClass>> response = testRestTemplate.exchange(this.baseUrl + "/15",
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {
                });
        LanguageClass languageClasses = response.getBody().getData();

        // Assert
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(),
                "HTTP Status code should be 404");
    }

    @Test
    @DisplayName("Check editLanguageClass with valid input (PUT)")
    @Order(7)
    void testEditLanguageClassSuccess() throws Exception {
        // Arrange
        JSONObject updatedLanguageClass = new JSONObject();
        updatedLanguageClass.put("title", "Updated Title");
        updatedLanguageClass.put("time", "11:00");
        updatedLanguageClass.put("description", "Updated Description");
        updatedLanguageClass.put("category", "French");
        updatedLanguageClass.put("dayOfWeek", 1);
        updatedLanguageClass.put("city", "Warsaw");
        updatedLanguageClass.put("address", "271 Street");
        updatedLanguageClass.put("postalCode", "V1 712L");
        updatedLanguageClass.put("province", "MB");
        updatedLanguageClass.put("country", "Updated Country");
        updatedLanguageClass.put("totalSpots", 10);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);

        HttpEntity<String> request = new HttpEntity<>(updatedLanguageClass.toString(), headers);

        // Act
        ResponseEntity<HttpResponse<ReturnLanguageClass>> response = testRestTemplate.exchange(this.baseUrl + "/1",
                HttpMethod.PUT,
                request,
                new ParameterizedTypeReference<>(){});

        ReturnLanguageClass returnLanguageClass = response.getBody().getData();

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(),
                "HTTP Status code should be 200");
        Assertions.assertNotNull(returnLanguageClass.getId(),
                "Response should contain UserID in a response header");
        Assertions.assertEquals(1, returnLanguageClass.getId(),
                "Size of retrieved Language classes seems to be incorrect");
        Assertions.assertEquals(updatedLanguageClass.get("title"), returnLanguageClass.getTitle(),
                "Size of retrieved Language classes seems to be incorrect");
        Assertions.assertEquals(updatedLanguageClass.get("description"), returnLanguageClass.getDescription(),
                "Size of retrieved Language classes seems to be incorrect");
        Assertions.assertEquals(updatedLanguageClass.get("totalSpots"), returnLanguageClass.getTotalSpots(),
                "Size of retrieved Language classes seems to be incorrect");
    }

    @Test
    @DisplayName("Check editLanguageClass with non-existent id (PUT)")
    @Order(8)
    void testEditLanguageClassErrorWithNonExistentId() throws Exception {
        // Arrange
        JSONObject updatedLanguageClass = new JSONObject();
        updatedLanguageClass.put("title", "Updated Title");
        updatedLanguageClass.put("time", "11:00");
        updatedLanguageClass.put("description", "Updated Description");
        updatedLanguageClass.put("category", "French");
        updatedLanguageClass.put("dayOfWeek", 1);
        updatedLanguageClass.put("city", "Warsaw");
        updatedLanguageClass.put("address", "271 Street");
        updatedLanguageClass.put("postalCode", "V1 712L");
        updatedLanguageClass.put("province", "MB");
        updatedLanguageClass.put("country", "Updated Country");
        updatedLanguageClass.put("totalSpots", 10);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);

        HttpEntity<String> request = new HttpEntity<>(updatedLanguageClass.toString(), headers);

        // Act
        ResponseEntity<HttpResponse<ReturnLanguageClass>> response = testRestTemplate.exchange(this.baseUrl + "/15",
                HttpMethod.PUT,
                request,
                new ParameterizedTypeReference<>(){});

        ReturnLanguageClass returnLanguageClass = response.getBody().getData();

        // Assert
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(),
                "HTTP Status code should be 404");
    }

    @Test
    @DisplayName("Check editLanguageClass with invalid input (PUT)")
    @Order(9)
    void testEditLanguageClassErrorWithInvalidInput() throws Exception {
        // Arrange
        JSONObject updatedLanguageClass = new JSONObject();
        updatedLanguageClass.put("title", "");
        updatedLanguageClass.put("time", "11:00");
        updatedLanguageClass.put("description", "");
        updatedLanguageClass.put("category", "French");
        updatedLanguageClass.put("dayOfWeek", 1);
        updatedLanguageClass.put("city", "Warsaw");
        updatedLanguageClass.put("address", "271 Street");
        updatedLanguageClass.put("postalCode", "V1 712L");
        updatedLanguageClass.put("province", "MB");
        updatedLanguageClass.put("country", "");
        updatedLanguageClass.put("totalSpots", 10);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);

        HttpEntity<String> request = new HttpEntity<>(updatedLanguageClass.toString(), headers);

        // Act
        ResponseEntity<HttpResponse<ReturnLanguageClass>> response = testRestTemplate.exchange(this.baseUrl + "/1",
                HttpMethod.PUT,
                request,
                new ParameterizedTypeReference<>(){});

        ReturnLanguageClass returnLanguageClass = response.getBody().getData();

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
                "HTTP Status code should be 400");
        Assertions.assertEquals("Title is required", returnLanguageClass.getTitle(),
                "Returned user's full name seems to be incorrect");
        Assertions.assertEquals("Description is required", returnLanguageClass.getDescription(),
                "Returned user's full name seems to be incorrect");
        Assertions.assertEquals("Country is required", returnLanguageClass.getCountry(),
                "Returned user's full name seems to be incorrect");
    }

    @Test
    @DisplayName("Check deleteLanguageClass with valid input (DELETE)")
    @Order(10)
    void testDeleteLanguageClassSuccess() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);

        HttpEntity request = new HttpEntity(headers);

        // Act
        ResponseEntity response = testRestTemplate.exchange(this.baseUrl + "/3",
                HttpMethod.DELETE,
                request,
                new ParameterizedTypeReference<>(){});

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(),
                "HTTP Status code should be 200");
        }

    @Test
    @DisplayName("Check deleteLanguageClass with non-existent id (DELETE)")
    @Order(11)
    void testDeleteLanguageClassErrorWithNonExistentId() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);

        HttpEntity request = new HttpEntity(headers);

        // Act
        ResponseEntity response = testRestTemplate.exchange(this.baseUrl + "/15",
                HttpMethod.DELETE,
                request,
                new ParameterizedTypeReference<>(){});

        // Assert
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(),
                "HTTP Status code should be 404");
     }

    @Test
    @DisplayName("Check attendLanguageClass with valid input (POST)")
    @Order(12)
    void testAttendLanguageClassSuccess() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);

        HttpEntity request = new HttpEntity(headers);

        // Act
        ResponseEntity response = testRestTemplate.exchange(this.baseUrl + "/2/attend",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>(){});

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(),
                "HTTP Status code should be 200");
    }

    @Test
    @DisplayName("Check attendLanguageClass with non-existent id (POST)")
    @Order(13)
    void testAttendLanguageClassErrorWithNonExistentId() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);

        HttpEntity request = new HttpEntity(headers);

        // Act
        ResponseEntity response = testRestTemplate.exchange(this.baseUrl + "/5/attend",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>(){});

        // Assert
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(),
                "HTTP Status code should be 404");
        }

    @Test
    @DisplayName("Check attendLanguageClass already attending (POST)")
    @Order(14)
    void testAttendLanguageClassErrorAlreadyAttending() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);

        HttpEntity request = new HttpEntity(headers);

        // Act
        ResponseEntity response = testRestTemplate.exchange(this.baseUrl + "/2/attend",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>(){});

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
                "HTTP Status code should be 400");
    }

    @Test
    @DisplayName("Check abandonLanguageClass with valid input (DELETE)")
    @Order(15)
    void testAbandonLanguageClassSuccess() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);

        HttpEntity request = new HttpEntity(headers);

        // Act
        ResponseEntity response = testRestTemplate.exchange(this.baseUrl + "/2/abandon",
                HttpMethod.DELETE,
                request,
                new ParameterizedTypeReference<>(){});

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(),
                "HTTP Status code should be 200");
    }

    @Test
    @DisplayName("Check abandonLanguageClass with non-existent id (DELETE)")
    @Order(16)
    void testAbandonLanguageClassErrorWithNonExistentId() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);

        HttpEntity request = new HttpEntity(headers);

        // Act
        ResponseEntity response = testRestTemplate.exchange(this.baseUrl + "/12/abandon",
                HttpMethod.DELETE,
                request,
                new ParameterizedTypeReference<>(){});

        // Assert
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(),
                "HTTP Status code should be 404");
        }

    @Test
    @DisplayName("Check abandonLanguageClass not attending (DELETE)")
    @Order(17)
    void testAttendLanguageClassErrorNotAttending() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);

        HttpEntity request = new HttpEntity(headers);

        // Act
        ResponseEntity response = testRestTemplate.exchange(this.baseUrl + "/2/abandon",
                HttpMethod.DELETE,
                request,
                new ParameterizedTypeReference<>(){});

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
                "HTTP Status code should be 400");
    }

    @Test
    @DisplayName("Check abandonLanguageClass hosted (DELETE)")
    @Order(18)
    void testAttendLanguageClassErrorHosted() throws Exception {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);

        HttpEntity request = new HttpEntity(headers);

        // Act
        ResponseEntity response = testRestTemplate.exchange(this.baseUrl + "/1/abandon",
                HttpMethod.DELETE,
                request,
                new ParameterizedTypeReference<>(){});

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
                "HTTP Status code should be 400");
    }
}