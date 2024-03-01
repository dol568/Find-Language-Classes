package com.springbootangularcourses.springbootbackend.system.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbootangularcourses.springbootbackend.system.ErrorResponse;
import com.springbootangularcourses.springbootbackend.system.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class CustomResponseEntityExceptionHandler{

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public final ResponseEntity<HttpResponse> handleObjectNotFoundException(ObjectNotFoundException ex) {
        HttpResponse response = HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .message(ex.getMessage())
                .status(NOT_FOUND)
                .statusCode(NOT_FOUND.value())
                .build();
        return new ResponseEntity<>(response, NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public final ResponseEntity<HttpResponse> handleValidationException(MethodArgumentNotValidException ex) {
        List<ObjectError> errors = ex.getBindingResult().getAllErrors();
        Map<String, String> map = new HashMap<>(errors.size());
        errors.forEach((error) -> {
            String key = ((FieldError) error).getField();
            String val = error.getDefaultMessage();
            map.put(key, val);
        });
        HttpResponse response = HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .message("Provided arguments are invalid, see data for details.")
                .data(map)
                .status(BAD_REQUEST)
                .statusCode(BAD_REQUEST.value())
                .build();
        return new ResponseEntity<>(response, BAD_REQUEST);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    @ResponseStatus(BAD_REQUEST)
    public final ResponseEntity<HttpResponse> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException ex) {
        HttpResponse response = HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .message(ex.getMessage())
                .status(BAD_REQUEST)
                .statusCode(BAD_REQUEST.value())
                .build();
        return new ResponseEntity<>(response, BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public final ResponseEntity<Object> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        HttpResponse response = HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .message(ex.getMessage())
                .status(NOT_FOUND)
                .statusCode(NOT_FOUND.value())
                .build();
        return new ResponseEntity<>(response, NOT_FOUND);
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(UNAUTHORIZED)
    public final ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException ex) {
        HttpResponse response = HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .message(ex.getMessage())
                .status(UNAUTHORIZED)
                .statusCode(UNAUTHORIZED.value())
                .build();
        return new ResponseEntity<>(response, UNAUTHORIZED);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(BAD_REQUEST)
    public final ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex) {
        HttpResponse response = HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .message(ex.getMessage())
                .status(BAD_REQUEST)
                .statusCode(BAD_REQUEST.value())
                .build();
        return new ResponseEntity<>(response, BAD_REQUEST);
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    @ResponseStatus(UNAUTHORIZED)
    public final ResponseEntity<Object> handleInsufficientAuthenticationException(InsufficientAuthenticationException ex) {
        HttpResponse response = HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .message("Login credentials are missing.")
                .status(UNAUTHORIZED)
                .statusCode(UNAUTHORIZED.value())
                .build();
        return new ResponseEntity<>(response, UNAUTHORIZED);
    }

    @ExceptionHandler(AccountStatusException.class)
    @ResponseStatus(UNAUTHORIZED)
    public final ResponseEntity<Object> handleAccountStatusException(AccountStatusException ex) {
        HttpResponse response = HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .message("User account is abnormal.")
                .status(UNAUTHORIZED)
                .statusCode(UNAUTHORIZED.value())
                .build();
        return new ResponseEntity<>(response, UNAUTHORIZED);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public final ResponseEntity<ErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        ErrorResponse response = ErrorResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .message("This API endpoint is not found.")
                .status(NOT_FOUND)
                .statusCode(NOT_FOUND.value())
                .build();
        return new ResponseEntity<>(response, NOT_FOUND);
    }

    @ExceptionHandler({HttpClientErrorException.class, HttpServerErrorException.class})
    ResponseEntity<HttpResponse> handleRestClientException(HttpStatusCodeException ex) throws JsonProcessingException {

        String exceptionMessage = ex.getMessage();

        exceptionMessage = exceptionMessage.replace("<EOL>", "\n");

        String jsonPart = exceptionMessage.substring(exceptionMessage.indexOf("{"), exceptionMessage.lastIndexOf("}") + 1);

        ObjectMapper mapper = new ObjectMapper();

        JsonNode rootNode = mapper.readTree(jsonPart);

        String formattedExceptionMessage = rootNode.path("error").path("message").asText();

        HttpResponse response = HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .message(formattedExceptionMessage)
                .status((HttpStatus) ex.getStatusCode())
                .statusCode(ex.getStatusCode().value())
                .build();
        return new ResponseEntity<>(response, (HttpStatus) ex.getStatusCode());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public final ResponseEntity<Object> handleOtherException(Exception ex) {
        HttpResponse response = HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .message("A server internal error occurs.")
                .status(INTERNAL_SERVER_ERROR)
                .statusCode(INTERNAL_SERVER_ERROR.value())
                .build();
        return new ResponseEntity<>(response, INTERNAL_SERVER_ERROR);
    }
}
