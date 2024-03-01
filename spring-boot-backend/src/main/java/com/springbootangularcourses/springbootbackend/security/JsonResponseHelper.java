package com.springbootangularcourses.springbootbackend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbootangularcourses.springbootbackend.system.ErrorResponse;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;

public class JsonResponseHelper {
    public static void writeJsonResponse(HttpServletResponse response, ErrorResponse errorResponse,
                                         HttpStatus httpStatus)
            throws IOException {
        setResponseHeader(response, httpStatus);
        writeResponse(response, errorResponse);
    }

    private static void writeResponse(HttpServletResponse response, ErrorResponse errorResponse)
            throws IOException {
        ServletOutputStream outputStream = response.getOutputStream();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(outputStream, errorResponse);

        outputStream.flush();
    }

    private static void setResponseHeader(HttpServletResponse response, HttpStatus httpStatus) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(httpStatus.value());
    }
}