package com.example.warehouse.dto;

import com.example.warehouse.model.Inventory;
import com.example.warehouse.model.ProductDetails;

/**
 * Aggregates a single warehouse inventory record with its MongoDB enrichment.
 * Used for single-item detail views.
 */
public record FullProductDTO(Inventory inventoryData, ProductDetails extraDetails) {}
