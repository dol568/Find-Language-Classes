package com.springbootangularcourses.springbootbackend.system;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

@Data
@SuperBuilder
public class HttpResponse<T> {
    private String timeStamp;
    private int statusCode;
    private HttpStatus status;
    private String message;
    private T data;

    public HttpResponse(String timeStamp, int statusCode, HttpStatus status, String message) {
        this.timeStamp = timeStamp;
        this.statusCode = statusCode;
        this.status = status;
        this.message = message;
    }
}
