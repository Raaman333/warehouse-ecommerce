package com.example.warehouse.dto;

/** Cart checkout payload: a map of prodId -> quantity */
public record CheckoutRequest(java.util.Map<String, Integer> items) {}
