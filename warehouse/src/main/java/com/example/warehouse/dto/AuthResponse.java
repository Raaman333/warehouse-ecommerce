package com.example.warehouse.dto;

/** JWT token + user metadata returned after successful authentication */
public record AuthResponse(String token, String username, String role) {}
