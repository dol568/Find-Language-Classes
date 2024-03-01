package com.springbootangularcourses.springbootbackend.system;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

@Data
@SuperBuilder
public class ErrorResponse {
    private String timeStamp;
    private int statusCode;
    public HttpStatus status;
    protected String message;
}
