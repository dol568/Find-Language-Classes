package com.springbootangularcourses.springbootbackend.training_class;

import com.springbootangularcourses.springbootbackend.domain.TrainingClass;
import com.springbootangularcourses.springbootbackend.domain.dto.ReturnTrainingClass;
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
class TrainingClassControllerIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    private String authorizationToken;

    private final String baseUrl = "/api/trainingClasses";

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
    @DisplayName("Check addTrainingClass with valid input (POST)")
    @Order(2)
    void testAddTrainingClassSuccess() throws Exception {
        // Arrange
        JSONObject newTrainingClass = new JSONObject();
        newTrainingClass.put("title", "Beginner French Class");
        newTrainingClass.put("time", "11:00");
        newTrainingClass.put("description", "Class for beginners");
        newTrainingClass.put("category", "French");
        newTrainingClass.put("dayOfWeek", 1);
        newTrainingClass.put("city", "Warsaw");
        newTrainingClass.put("address", "271 Street");
        newTrainingClass.put("postalCode", "V1 712L");
        newTrainingClass.put("province", "MB");
        newTrainingClass.put("country", "Poland");
        newTrainingClass.put("totalSpots", 10);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);

        HttpEntity<String> request = new HttpEntity<>(newTrainingClass.toString(), headers);

        // Act
        ResponseEntity<HttpResponse<ReturnTrainingClass>> response = testRestTemplate.exchange(this.baseUrl,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>(){});

        ReturnTrainingClass returnTrainingClass = response.getBody().getData();

        // Assert
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode(),
                "HTTP Status code should be 200");
        Assertions.assertNotNull(returnTrainingClass.getId(),
                "Response should contain UserID in a response header");
        Assertions.assertEquals(newTrainingClass.get("title"), returnTrainingClass.getTitle(),
                "Size of retrieved training classes seems to be incorrect");
        Assertions.assertEquals(newTrainingClass.get("description"), returnTrainingClass.getDescription(),
                "Size of retrieved training classes seems to be incorrect");
        Assertions.assertEquals(newTrainingClass.get("totalSpots"), returnTrainingClass.getTotalSpots(),
                "Size of retrieved training classes seems to be incorrect");
    }

    @Test
    @DisplayName("Check addTrainingClass with invalid input (POST)")
    @Order(3)
    void testAddTrainingClassErrorWithInvalidInput() throws Exception {
        // Arrange
        JSONObject newTrainingClass = new JSONObject();
        newTrainingClass.put("title", "");
        newTrainingClass.put("time", "");
        newTrainingClass.put("description", "");
        newTrainingClass.put("category", "French");
        newTrainingClass.put("dayOfWeek", 1);
        newTrainingClass.put("city", "Warsaw");
        newTrainingClass.put("address", "271 Street");
        newTrainingClass.put("postalCode", "V1 712L");
        newTrainingClass.put("province", "MB");
        newTrainingClass.put("country", "Poland");
        newTrainingClass.put("totalSpots", 10);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);

        HttpEntity<String> request = new HttpEntity<>(newTrainingClass.toString(), headers);

        // Act
        ResponseEntity<HttpResponse<ReturnTrainingClass>> response = testRestTemplate.exchange(this.baseUrl,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>(){});

        ReturnTrainingClass returnTrainingClass = response.getBody().getData();

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
                "HTTP Status code should be 400");
        Assertions.assertEquals("Title is required", returnTrainingClass.getTitle(),
                "Returned user's full name seems to be incorrect");
        Assertions.assertEquals("Description is required", returnTrainingClass.getDescription(),
                "Returned user's full name seems to be incorrect");
        Assertions.assertEquals("Time is required", returnTrainingClass.getTime(),
                "Returned user's full name seems to be incorrect");
    }

    @Test
    @DisplayName("Check getTrainingClasses (GET)")
    @Order(4)
    void testGetTrainingClassesSuccess() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);

        HttpEntity request = new HttpEntity(headers);

        // Act
        ResponseEntity<HttpResponse<List<TrainingClass>>> response = testRestTemplate.exchange(this.baseUrl,
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {
                });
        List<TrainingClass> trainingClasses = response.getBody().getData();

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(),
                "HTTP Status code should be 200");
        Assertions.assertTrue(trainingClasses.size() == 3,
                "Size of retrieved training classes seems to be incorrect");
    }

    @Test
    @DisplayName("Check getTrainingClass (GET)")
    @Order(5)
    void testGetTrainingClassSuccess() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);

        HttpEntity request = new HttpEntity(headers);

        // Act
        ResponseEntity<HttpResponse<TrainingClass>> response = testRestTemplate.exchange(this.baseUrl + "/3",
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {
                });
        TrainingClass trainingClasses = response.getBody().getData();

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(),
                "HTTP Status code should be 200");
        Assertions.assertEquals(3, trainingClasses.getId(),
                "Size of retrieved training classes seems to be incorrect");
        Assertions.assertEquals("Beginner French Class", trainingClasses.getTitle(),
                "Size of retrieved training classes seems to be incorrect");
        Assertions.assertEquals(1, trainingClasses.getUserTrainingClasses().size(),
                "Size of retrieved training classes seems to be incorrect");
    }

    @Test
    @DisplayName("Check getTrainingClass with non-existent id (GET)")
    @Order(6)
    void testGetTrainingClassErrorIdNotFound() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);

        HttpEntity request = new HttpEntity(headers);

        // Act
        ResponseEntity<HttpResponse<TrainingClass>> response = testRestTemplate.exchange(this.baseUrl + "/15",
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {
                });
        TrainingClass trainingClasses = response.getBody().getData();

        // Assert
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(),
                "HTTP Status code should be 404");
    }

    @Test
    @DisplayName("Check editTrainingClass with valid input (PUT)")
    @Order(7)
    void testEditTrainingClassSuccess() throws Exception {
        // Arrange
        JSONObject updatedTrainingClass = new JSONObject();
        updatedTrainingClass.put("title", "Updated Title");
        updatedTrainingClass.put("time", "11:00");
        updatedTrainingClass.put("description", "Updated Description");
        updatedTrainingClass.put("category", "French");
        updatedTrainingClass.put("dayOfWeek", 1);
        updatedTrainingClass.put("city", "Warsaw");
        updatedTrainingClass.put("address", "271 Street");
        updatedTrainingClass.put("postalCode", "V1 712L");
        updatedTrainingClass.put("province", "MB");
        updatedTrainingClass.put("country", "Updated Country");
        updatedTrainingClass.put("totalSpots", 10);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);

        HttpEntity<String> request = new HttpEntity<>(updatedTrainingClass.toString(), headers);

        // Act
        ResponseEntity<HttpResponse<ReturnTrainingClass>> response = testRestTemplate.exchange(this.baseUrl + "/1",
                HttpMethod.PUT,
                request,
                new ParameterizedTypeReference<>(){});

        ReturnTrainingClass returnTrainingClass = response.getBody().getData();

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(),
                "HTTP Status code should be 200");
        Assertions.assertNotNull(returnTrainingClass.getId(),
                "Response should contain UserID in a response header");
        Assertions.assertEquals(1, returnTrainingClass.getId(),
                "Size of retrieved training classes seems to be incorrect");
        Assertions.assertEquals(updatedTrainingClass.get("title"), returnTrainingClass.getTitle(),
                "Size of retrieved training classes seems to be incorrect");
        Assertions.assertEquals(updatedTrainingClass.get("description"), returnTrainingClass.getDescription(),
                "Size of retrieved training classes seems to be incorrect");
        Assertions.assertEquals(updatedTrainingClass.get("totalSpots"), returnTrainingClass.getTotalSpots(),
                "Size of retrieved training classes seems to be incorrect");
    }

    @Test
    @DisplayName("Check editTrainingClass with non-existent id (PUT)")
    @Order(8)
    void testEditTrainingClassErrorWithNonExistentId() throws Exception {
        // Arrange
        JSONObject updatedTrainingClass = new JSONObject();
        updatedTrainingClass.put("title", "Updated Title");
        updatedTrainingClass.put("time", "11:00");
        updatedTrainingClass.put("description", "Updated Description");
        updatedTrainingClass.put("category", "French");
        updatedTrainingClass.put("dayOfWeek", 1);
        updatedTrainingClass.put("city", "Warsaw");
        updatedTrainingClass.put("address", "271 Street");
        updatedTrainingClass.put("postalCode", "V1 712L");
        updatedTrainingClass.put("province", "MB");
        updatedTrainingClass.put("country", "Updated Country");
        updatedTrainingClass.put("totalSpots", 10);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);

        HttpEntity<String> request = new HttpEntity<>(updatedTrainingClass.toString(), headers);

        // Act
        ResponseEntity<HttpResponse<ReturnTrainingClass>> response = testRestTemplate.exchange(this.baseUrl + "/15",
                HttpMethod.PUT,
                request,
                new ParameterizedTypeReference<>(){});

        ReturnTrainingClass returnTrainingClass = response.getBody().getData();

        // Assert
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(),
                "HTTP Status code should be 404");
    }

    @Test
    @DisplayName("Check editTrainingClass with invalid input (PUT)")
    @Order(9)
    void testEditTrainingClassErrorWithInvalidInput() throws Exception {
        // Arrange
        JSONObject updatedTrainingClass = new JSONObject();
        updatedTrainingClass.put("title", "");
        updatedTrainingClass.put("time", "11:00");
        updatedTrainingClass.put("description", "");
        updatedTrainingClass.put("category", "French");
        updatedTrainingClass.put("dayOfWeek", 1);
        updatedTrainingClass.put("city", "Warsaw");
        updatedTrainingClass.put("address", "271 Street");
        updatedTrainingClass.put("postalCode", "V1 712L");
        updatedTrainingClass.put("province", "MB");
        updatedTrainingClass.put("country", "");
        updatedTrainingClass.put("totalSpots", 10);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);

        HttpEntity<String> request = new HttpEntity<>(updatedTrainingClass.toString(), headers);

        // Act
        ResponseEntity<HttpResponse<ReturnTrainingClass>> response = testRestTemplate.exchange(this.baseUrl + "/1",
                HttpMethod.PUT,
                request,
                new ParameterizedTypeReference<>(){});

        ReturnTrainingClass returnTrainingClass = response.getBody().getData();

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
                "HTTP Status code should be 400");
        Assertions.assertEquals("Title is required", returnTrainingClass.getTitle(),
                "Returned user's full name seems to be incorrect");
        Assertions.assertEquals("Description is required", returnTrainingClass.getDescription(),
                "Returned user's full name seems to be incorrect");
        Assertions.assertEquals("Country is required", returnTrainingClass.getCountry(),
                "Returned user's full name seems to be incorrect");
    }

    @Test
    @DisplayName("Check deleteTrainingClass with valid input (DELETE)")
    @Order(10)
    void testDeleteTrainingClassSuccess() {
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
    @DisplayName("Check deleteTrainingClass with non-existent id (DELETE)")
    @Order(11)
    void testDeleteTrainingClassErrorWithNonExistentId() {
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
    @DisplayName("Check attendTrainingClass with valid input (POST)")
    @Order(12)
    void testAttendTrainingClassSuccess() {
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
    @DisplayName("Check attendTrainingClass with non-existent id (POST)")
    @Order(13)
    void testAttendTrainingClassErrorWithNonExistentId() {
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
    @DisplayName("Check attendTrainingClass already attending (POST)")
    @Order(14)
    void testAttendTrainingClassErrorAlreadyAttending() {
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
    @DisplayName("Check abandonTrainingClass with valid input (DELETE)")
    @Order(15)
    void testAbandonTrainingClassSuccess() {
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
    @DisplayName("Check abandonTrainingClass with non-existent id (DELETE)")
    @Order(16)
    void testAbandonTrainingClassErrorWithNonExistentId() {
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
    @DisplayName("Check abandonTrainingClass not attending (DELETE)")
    @Order(17)
    void testAttendTrainingClassErrorNotAttending() {
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
    @DisplayName("Check abandonTrainingClass hosted (DELETE)")
    @Order(18)
    void testAttendTrainingClassErrorHosted() throws Exception {
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