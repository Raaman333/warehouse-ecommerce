package com.example.warehouse.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

/**
 * Rich product metadata stored in MongoDB {@code product_details} collection.
 * Linked to the MySQL inventory record via {@code prodId}.
 */
@Document(collection = "product_details")
public class ProductDetails {

    @Id
    private String id;

    /** FK to MySQL inventory.prod_id */
    @Indexed(unique = true)
    private String prodId;

    private String description;
    private String brand;
    private String imageUrl;

    /** e.g. {"Weight": "1.2 kg", "Color": "Black", "Warranty": "2 years"} */
    private Map<String, String> specifications;

    /** Optional list of searchable tags */
    private List<String> tags;

    public String getId()                           { return id; }
    public void setId(String v)                     { this.id = v; }

    public String getProdId()                       { return prodId; }
    public void setProdId(String v)                 { this.prodId = v; }

    public String getDescription()                  { return description; }
    public void setDescription(String v)            { this.description = v; }

    public String getBrand()                        { return brand; }
    public void setBrand(String v)                  { this.brand = v; }

    public String getImageUrl()                     { return imageUrl; }
    public void setImageUrl(String v)               { this.imageUrl = v; }

    public Map<String, String> getSpecifications()  { return specifications; }
    public void setSpecifications(Map<String, String> v) { this.specifications = v; }

    public List<String> getTags()                   { return tags; }
    public void setTags(List<String> v)             { this.tags = v; }
}
