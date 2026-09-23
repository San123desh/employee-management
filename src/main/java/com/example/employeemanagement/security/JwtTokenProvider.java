package com.example.employeemanagement.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;


@Component
public class JwtTokenProvider {


    private final SecretKey key;
    private static final long EXPIRATION_MS = 1000 * 60 * 60; //1 hour


    // TODO : PROD.. put in .env / application-prod.properties
    // production, the secret comes from your deployment platform's secrets manager (AWS Secrets Manager, GCP Secret Manager, etc.) — never from a file.
    //secret key defined in application.properties
    public JwtTokenProvider(@Value("${app.jwt.secret}") String secret){
        this.key = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8));
    }


    public String generateToken(String username){
        Date now = new Date();
        Date expiry = new Date(now.getTime() + EXPIRATION_MS);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }


    //extract username from token
    public String getUsername(String token){
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // Token Validate
    public boolean validateToken(String token){
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        }catch (JwtException | IllegalArgumentException e){
            return false;
        }
    }

}
