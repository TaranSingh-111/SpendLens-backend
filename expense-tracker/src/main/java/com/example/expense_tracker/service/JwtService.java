package com.example.expense_tracker.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expirationMs;
    private final long refreshExpirationMs;

    //Generating the token
    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs,
            @Value("${jwt.refresh.expiration-ms}") long refreshExpirationMs
    ) {
        this.secretKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
        this.expirationMs = expirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    public String generateAccessToken(String email){
        return generateToken(email, expirationMs, "access");
    }

    public String generateRefreshToken(String email){
        return generateToken(email, refreshExpirationMs, "refresh");
    }

    public String generateToken(String email, long lifetimeMs, String tokenType){
        Date now = new Date();
        Date expiration = new Date(now.getTime() + lifetimeMs);

        return Jwts.builder()
                .subject(email)
                .claim("token_type", tokenType)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    //verifying the signature and expiration
    //extracting the claims from the token
    private Claims extractClaims(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    //getting the subject of the token
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    //checking token type

    public boolean isAccessToken(String token){
        return "access".equals(
                extractClaims(token).get("token_type", String.class)
        );
    }

    public boolean isRefreshToken(String token){
        return "refresh".equals(
                extractClaims(token).get("token_type", String.class)
        );
    }

    //matches the username with the userDetails provided
    public boolean isTokenValid(String token, UserDetails userDetails){

        try{
            String username = extractUsername(token);

            return isAccessToken(token) && username.equals(userDetails.getUsername());
        }catch (Exception exception){
            return false;
        }
    }
}
