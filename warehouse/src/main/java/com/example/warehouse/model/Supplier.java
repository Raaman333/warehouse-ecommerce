package com.example.warehouse.model;

/**
 * Supplier entity — stored in MySQL {@code supplier} table.
 */
public class Supplier {
    private Long   supplierId;
    private String name;
    private String contactEmail;
    private String country;

    public Long getSupplierId()              { return supplierId; }
    public void setSupplierId(Long v)        { this.supplierId = v; }

    public String getName()                  { return name; }
    public void setName(String v)            { this.name = v; }

    public String getContactEmail()          { return contactEmail; }
    public void setContactEmail(String v)    { this.contactEmail = v; }

    public String getCountry()               { return country; }
    public void setCountry(String v)         { this.country = v; }
}
