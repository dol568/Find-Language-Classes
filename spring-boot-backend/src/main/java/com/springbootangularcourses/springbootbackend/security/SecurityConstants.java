package com.springbootangularcourses.springbootbackend.security;

public class SecurityConstants {
    public static final String SECRET = "6A576D5A7134743777217A25432A462D4A614E645267556B5870327235753878";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final long EXPIRATION_TIME = 600_000_000;
    public static final String AUTHORITIES = "authorities";
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";
    public static final String TOKEN_REQUIRED = "Token is required";
    public static final String SUBJECT_REQUIRED = "Subject is required";
    public static final String HEADER_STRING = "Authorization";
}
