// src/main/java/org/andi/librarymanagementbackend/config/JwtUtil.java
package org.andi.librarymanagementbackend.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Utility class for generating and validating JWT tokens using HS512.
 */
@Component
public class JwtUtil {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private final int jwtExpirationMs = 3600_000; // 1 hour

    /**
     * Generate a JWT token for the given username.
     *
     * @param username the subject to embed in the token
     * @return a signed JWT token string
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key)
                .compact();
    }

    /**
     * Extract the username (subject) from a JWT token.
     *
     * @param token the JWT token
     * @return the username embedded in the token
     * @throws JwtException if token parsing fails
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Validate a JWT token.
     *
     * @param authToken the token to validate
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}

