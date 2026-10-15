package com.example.warehouse.repository;

import com.example.warehouse.model.AppUser;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * User persistence via JdbcTemplate — keeps auth fully in MySQL
 * so no MongoDB credentials are needed for login flows.
 */
@Repository
public class UserRepository {

    private final JdbcTemplate jdbc;

    public UserRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<AppUser> USER_ROW_MAPPER = (rs, rowNum) -> {
        AppUser u = new AppUser();
        u.setId(rs.getLong("id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setRole(rs.getString("role"));
        return u;
    };

    public Optional<AppUser> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        var results = jdbc.query(sql, USER_ROW_MAPPER, username);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public boolean existsByUsername(String username) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM users WHERE username = ?", Integer.class, username);
        return count != null && count > 0;
    }

    /**
     * Registers a new user with a pre-hashed password.
     * Default role is CUSTOMER unless explicitly set.
     */
    public AppUser save(AppUser user) {
        String role = (user.getRole() != null) ? user.getRole() : "CUSTOMER";
        jdbc.update(
                "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)",
                user.getUsername(), user.getPasswordHash(), role);
        return findByUsername(user.getUsername()).orElseThrow();
    }
}
