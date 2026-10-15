package com.example.warehouse.model;

/**
 * Represents a physical warehouse / fulfillment facility.
 * Persisted in MySQL {@code warehouse} table.
 */
public class Warehouse {
    private Long warehouseId;
    private String name;
    private String location;

    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}
