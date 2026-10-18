package com.example.expense_tracker.dto;

import com.example.expense_tracker.model.User;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        UserResponse user
) {
}
