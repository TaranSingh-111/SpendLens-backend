package com.example.expense_tracker.dto;

public record UserResponse(
        Integer id,
        String name,
        String email
) {
}
