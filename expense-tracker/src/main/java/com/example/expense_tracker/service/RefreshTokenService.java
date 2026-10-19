package com.example.expense_tracker.service;

import com.example.expense_tracker.exception.ResourceNotFoundException;
import com.example.expense_tracker.model.RefreshToken;
import com.example.expense_tracker.model.User;
import com.example.expense_tracker.repository.RefreshTokenRepository;
import com.example.expense_tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.sql.Ref;
import java.time.Instant;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final TokenHashService tokenHashService;
    private final long refreshExpirationMs;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                               UserRepository userRepository,
                               JwtService jwtService,
                               TokenHashService tokenHashService,
                               @Value("${jwt.refresh.expiration-ms}") long refreshExpirationMs
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.tokenHashService = tokenHashService;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    @Transactional
    public String createRefreshToken(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String token = jwtService.generateRefreshToken(email);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(tokenHashService.hash(token));
        refreshToken.setExpiresAt(
                Instant.now().plusMillis(refreshExpirationMs)
        );
        refreshToken.setRevoked(false);

        refreshTokenRepository.save(refreshToken);
        return token;
    }

    @Transactional(readOnly = true)
    public RefreshToken validateRefreshToken(String token){
        if(!jwtService.isRefreshToken(token)){
            throw new IllegalArgumentException("Invalid refresh token");
        }
        String tokenHash = tokenHashService.hash(token);

        RefreshToken storedToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if(storedToken.isRevoked()){
            throw new IllegalArgumentException("Refresh token revoked");
        }
        if(!storedToken.getExpiresAt().isAfter(Instant.now())){
            throw new IllegalArgumentException("Refresh token expired");
        }

        return storedToken;
    }

    @Transactional
    public void revokeRefreshToken(String token){
        String tokenHash = tokenHashService.hash(token);

        refreshTokenRepository.findByTokenHash(tokenHash)
                .ifPresent(storedToken -> {
                    storedToken.setRevoked(true);
                    refreshTokenRepository.save(storedToken);
                });
    }
}
