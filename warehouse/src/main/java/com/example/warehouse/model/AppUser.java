package com.example.warehouse.model;

/**
 * Application user stored in MySQL {@code users} table.
 * Passwords are BCrypt-hashed; never stored or returned in plaintext.
 */
public class AppUser {
    private Long   id;
    private String username;
    private String passwordHash;
    private String role;           // "CUSTOMER" | "ADMIN"

    public Long getId()                    { return id; }
    public void setId(Long v)              { this.id = v; }

    public String getUsername()            { return username; }
    public void setUsername(String v)      { this.username = v; }

    public String getPasswordHash()        { return passwordHash; }
    public void setPasswordHash(String v)  { this.passwordHash = v; }

    public String getRole()                { return role; }
    public void setRole(String v)          { this.role = v; }
}
