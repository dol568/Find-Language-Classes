package com.springbootangularcourses.springbootbackend.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.springbootangularcourses.springbootbackend.domain.UserPrincipal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.Instant;

import static com.springbootangularcourses.springbootbackend.security.SecurityConstants.*;

@Component
public class TokenProvider {

    public String generateToken(UserPrincipal userPrincipal) {
        String[] claims = getClaims(userPrincipal);

        return createToken(userPrincipal, claims);
    }

    private String createToken(UserPrincipal userPrincipal, String[] claims) {
        return JWT.create()
                .withSubject(userPrincipal.getUsername())
                .withArrayClaim(AUTHORITIES, claims)
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plusMillis(EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(SECRET));
    }

    private String[] getClaims(UserPrincipal userPrincipal) {
        return userPrincipal.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toArray(String[]::new);
    }
}
