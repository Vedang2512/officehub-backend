package com.officehub.util;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Generate JWT Token
    public String generateToken(
            Long userId,
            String email,
            Long organizationId,
            String role
    ) {

    	return Jwts.builder()
    	        .subject(email)
    	        .claim("userId", userId)
    	        .claim("organizationId", organizationId)
    	        .claim("role", role)
    	        .issuedAt(new Date())
    	        .expiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))
    	        .signWith(getSigningKey())
    	        .compact();
    }

    // Extract all claims
    private Claims extractClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Extract username (email)
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // Validate JWT Token
    public boolean validateToken(String token, UserDetails userDetails) {

        try {
            String username = extractUsername(token);

            return username.equals(userDetails.getUsername())
                    && !extractClaims(token).getExpiration().before(new Date());

        } catch (Exception e) {
            return false;
        }
    }

    // Check if token is expired
    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }
    
    public Long extractUserId(String token) {

        return extractClaims(token)
                .get("userId", Long.class);
    }


    public Long extractOrganizationId(String token) {

        return extractClaims(token)
                .get("organizationId", Long.class);
    }


    public String extractRole(String token) {

        return extractClaims(token)
                .get("role", String.class);
    }
}