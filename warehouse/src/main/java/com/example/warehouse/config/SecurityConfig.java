package com.example.warehouse.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Stateless JWT-based security configuration.
 * <ul>
 *   <li>Public: GET /api/inventory/**, GET /api/warehouses, POST /api/auth/**</li>
 *   <li>CUSTOMER: POST /api/inventory/cart/**, POST /api/inventory/checkout</li>
 *   <li>ADMIN: POST/PUT/DELETE /api/inventory/**, all admin operations</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtFilter jwtFilter, DaoAuthenticationProvider authenticationProvider) throws Exception {
        http
            .cors(org.springframework.security.config.Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Allow preflight OPTIONS requests from any origin
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // Public read-only catalog
                .requestMatchers(HttpMethod.GET,  "/api/inventory/**").permitAll()
                .requestMatchers(HttpMethod.GET,  "/api/warehouses").permitAll()
                .requestMatchers(HttpMethod.GET,  "/api/suppliers").permitAll()
                // Auth endpoints
                .requestMatchers("/api/auth/**").permitAll()
                // Cart & checkout — any authenticated user
                .requestMatchers("/api/inventory/cart/**").hasAnyRole("CUSTOMER", "ADMIN")
                .requestMatchers("/api/inventory/checkout").hasAnyRole("CUSTOMER", "ADMIN")
                // Admin-only mutations
                .requestMatchers(HttpMethod.POST,   "/api/inventory/add").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/inventory/update").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/inventory/delete/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
