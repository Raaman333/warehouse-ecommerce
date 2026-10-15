package com.example.warehouse.config;

import com.example.warehouse.model.AppUser;
import com.example.warehouse.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Ensures the default administrator account exists with the exact BCrypt hash
 * produced by the application's configured PasswordEncoder on startup.
 */
@Component
public class AdminUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbc;

    public AdminUserInitializer(UserRepository userRepository,
                                PasswordEncoder passwordEncoder,
                                JdbcTemplate jdbc) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jdbc = jdbc;
    }

    @Override
    public void run(String... args) {
        String encodedPassword = passwordEncoder.encode("Admin@123");
        if (userRepository.existsByUsername("admin")) {
            jdbc.update("UPDATE users SET password_hash = ?, role = 'ADMIN' WHERE username = 'admin'", encodedPassword);
        } else {
            AppUser admin = new AppUser();
            admin.setUsername("admin");
            admin.setPasswordHash(encodedPassword);
            admin.setRole("ADMIN");
            userRepository.save(admin);
        }
    }
}
