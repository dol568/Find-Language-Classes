package com.springbootangularcourses.springbootbackend.security;

import com.springbootangularcourses.springbootbackend.system.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Component
public class JWTAccessDeniedHandler implements AccessDeniedHandler {
    public static final String MESSAGE
            = "Access denied. You do not have sufficient permissions to access this resource";

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .message(MESSAGE)
                .status(UNAUTHORIZED)
                .statusCode(UNAUTHORIZED.value())
                .build();
        JsonResponseHelper.writeJsonResponse(response, errorResponse, UNAUTHORIZED);
    }
}
