package com.example.expense_tracker.controller;

import com.example.expense_tracker.dto.*;
import com.example.expense_tracker.exception.ResourceNotFoundException;
import com.example.expense_tracker.model.User;
import com.example.expense_tracker.repository.RefreshTokenRepository;
import com.example.expense_tracker.repository.UserRepository;
import com.example.expense_tracker.service.JwtService;
import com.example.expense_tracker.service.RefreshTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, RefreshTokenService refreshTokenService, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request){
        System.out.println("Login controller reached");

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        String email = authentication.getName();

        String accessToken = jwtService.generateAccessToken(email);
        String refreshToken = refreshTokenService.createRefreshToken(email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail()
        );

        return new LoginResponse(
                accessToken,
                refreshToken,
                "Bearer",
                900,
                userResponse
        );
    }

    @PostMapping("/refresh")
    public LoginResponse refresh(@RequestBody RefreshTokenRequest request){
        String oldRefreshToken = request.refreshToken();

        //validate the token and generate replacement
        String newRefreshToken = refreshTokenService.rotateRefreshToken(oldRefreshToken);

        //creating new access token
        String email = jwtService.extractUsername(oldRefreshToken);
        String newAccessToken = jwtService.generateAccessToken(email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail()
        );

        return new LoginResponse(
                newAccessToken,
                newRefreshToken,
                "Bearer",
                900,
                userResponse
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequest request){
        refreshTokenService.revokeRefreshToken(request.refreshToken());

        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials(BadCredentialsException e){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Invalid email or password"));
    }
}
