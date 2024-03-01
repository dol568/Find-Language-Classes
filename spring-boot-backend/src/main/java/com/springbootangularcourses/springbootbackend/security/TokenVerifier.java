package com.springbootangularcourses.springbootbackend.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import static com.springbootangularcourses.springbootbackend.security.SecurityConstants.*;

@Component
public class TokenVerifier {
    public void verifyTokenAndBuildAuthentication(String token, HttpServletRequest request) {
        String subject = verifyToken().verify(token).getSubject();

        if (isTokenValid(token, subject)) {
            Set<SimpleGrantedAuthority> grantedAuthorities = getSimpleGrantedAuthorities(token);

            UsernamePasswordAuthenticationToken authentication = buildAuthentication(request, subject, grantedAuthorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            SecurityContextHolder.clearContext();
        }
    }

    private UsernamePasswordAuthenticationToken buildAuthentication(HttpServletRequest request, String subject,
                                                                    Set<SimpleGrantedAuthority> grantedAuthorities) {

        UsernamePasswordAuthenticationToken authentication
                = new UsernamePasswordAuthenticationToken(subject, request, grantedAuthorities);
        if (request != null) {
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        }
        return authentication;
    }

    private Set<SimpleGrantedAuthority> getSimpleGrantedAuthorities(String token) {
        String[] claims = verifyToken().verify(token).getClaim(AUTHORITIES).asArray(String.class);

        return Arrays.stream(claims).map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    }

    private boolean isTokenValid(String token, String subject) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException(TOKEN_REQUIRED);
        } else if (subject == null || subject.isEmpty()) {
            throw new IllegalArgumentException(SUBJECT_REQUIRED);
        }
        return !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date expiresAt = verifyToken().verify(token).getExpiresAt();

        return expiresAt.before(new Date());
    }

    private JWTVerifier verifyToken() {
        try {
            Algorithm algorithm = Algorithm.HMAC512(SECRET);

            return JWT.require(algorithm).build();
        }
        catch (JWTVerificationException e) {
            throw new JWTVerificationException(TOKEN_CANNOT_BE_VERIFIED);
        }
    }
}
