package com.example.warehouse.dto;

import com.example.warehouse.model.ProductDetails;

import java.util.Set;

/**
 * Aggregates multi-warehouse stock for one {@code prodId} into a single
 * storefront card.  Total stock is the sum across all warehouses.
 */
public class GroupedProductDTO {

    private String          prodId;
    private String          prodName;
    private String          category;
    private Double          sellingPrice;
    private Integer         totalStock;
    private Set<Long>       warehouses;   // warehouse IDs holding this product
    private Set<String>     suppliers;    // formatted supplier names
    private ProductDetails  extraDetails; // MongoDB enrichment

    public String getProdId()                          { return prodId; }
    public void setProdId(String v)                    { this.prodId = v; }

    public String getProdName()                        { return prodName; }
    public void setProdName(String v)                  { this.prodName = v; }

    public String getCategory()                        { return category; }
    public void setCategory(String v)                  { this.category = v; }

    public Double getSellingPrice()                    { return sellingPrice; }
    public void setSellingPrice(Double v)              { this.sellingPrice = v; }

    public Integer getTotalStock()                     { return totalStock; }
    public void setTotalStock(Integer v)               { this.totalStock = v; }

    public Set<Long> getWarehouses()                   { return warehouses; }
    public void setWarehouses(Set<Long> v)             { this.warehouses = v; }

    public Set<String> getSuppliers()                  { return suppliers; }
    public void setSuppliers(Set<String> v)            { this.suppliers = v; }

    public ProductDetails getExtraDetails()            { return extraDetails; }
    public void setExtraDetails(ProductDetails v)      { this.extraDetails = v; }
}
