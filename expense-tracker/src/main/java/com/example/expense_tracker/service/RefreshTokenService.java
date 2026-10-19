package com.example.expense_tracker.service;

import com.example.expense_tracker.exception.InvalidRefreshTokenException;
import com.example.expense_tracker.exception.ResourceNotFoundException;
import com.example.expense_tracker.model.RefreshToken;
import com.example.expense_tracker.model.User;
import com.example.expense_tracker.repository.RefreshTokenRepository;
import com.example.expense_tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
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
    public String rotateRefreshToken(String token){
        //verifying the token
        RefreshToken storedToken = validateRefreshToken(token);

        //revoking the old token
        storedToken.setRevoked(true);
        refreshTokenRepository.saveAndFlush(storedToken);

        //creating the new token
        String email = storedToken.getUser().getEmail();
        return createRefreshToken(email);
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
        if (token == null || token.isBlank()) {
            throw new InvalidRefreshTokenException(
                    "Refresh token is required"
            );
        }

        if(!jwtService.isRefreshToken(token)){
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }
        String tokenHash = tokenHashService.hash(token);

        RefreshToken storedToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));

        if(storedToken.isRevoked()){
            throw new InvalidRefreshTokenException("Refresh token revoked");
        }
        if(!storedToken.getExpiresAt().isAfter(Instant.now())){
            throw new InvalidRefreshTokenException("Refresh token expired");
        }

        return storedToken;
    }

    @Transactional
    public void revokeRefreshToken(String token){
        if (token == null || token.isBlank()) {
            throw new InvalidRefreshTokenException(
                    "Refresh token is required"
            );
        }

        if(!jwtService.isRefreshToken(token)){
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }

        String tokenHash = tokenHashService.hash(token);

        refreshTokenRepository.findByTokenHash(tokenHash)
                .ifPresent(storedToken -> {
                    storedToken.setRevoked(true);
                    refreshTokenRepository.save(storedToken);
                });
    }
}
