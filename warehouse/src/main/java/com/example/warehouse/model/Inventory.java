package com.example.warehouse.model;

/**
 * Inventory line item: one product stocked at one warehouse.
 * Composite key: (warehouse_id, prod_id).
 * Persisted in MySQL {@code inventory} table via JdbcTemplate.
 */
public class Inventory {

    private Long   warehouseId;
    private String prodId;
    private String prodName;
    private Integer stockNumber;
    private Double  sellingPrice;
    private Long   supplierId;
    private String supplierName;  // populated by LEFT JOIN on supplier
    private String category;

    public Long getWarehouseId()               { return warehouseId; }
    public void setWarehouseId(Long v)         { this.warehouseId = v; }

    public String getProdId()                  { return prodId; }
    public void setProdId(String v)            { this.prodId = v; }

    public String getProdName()                { return prodName; }
    public void setProdName(String v)          { this.prodName = v; }

    public Integer getStockNumber()            { return stockNumber; }
    public void setStockNumber(Integer v)      { this.stockNumber = v; }

    public Double getSellingPrice()            { return sellingPrice; }
    public void setSellingPrice(Double v)      { this.sellingPrice = v; }

    public Long getSupplierId()                { return supplierId; }
    public void setSupplierId(Long v)          { this.supplierId = v; }

    public String getSupplierName()            { return supplierName; }
    public void setSupplierName(String v)      { this.supplierName = v; }

    public String getCategory()                { return category; }
    public void setCategory(String v)          { this.category = v; }
}
