package com.springbootangularcourses.springbootbackend.security;

import com.springbootangularcourses.springbootbackend.system.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@Component
public class JWTForbiddenEntryPoint extends Http403ForbiddenEntryPoint {

    public static final String MESSAGE = "Access denied. You need to login to access this resource";

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException arg2)
            throws IOException {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .message(MESSAGE)
                .status(FORBIDDEN)
                .statusCode(FORBIDDEN.value())
                .build();
        JsonResponseHelper.writeJsonResponse(response, errorResponse, FORBIDDEN);
    }
}
