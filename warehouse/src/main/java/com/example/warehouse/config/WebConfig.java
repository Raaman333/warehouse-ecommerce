package com.example.warehouse.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Explicit CORS configuration — allows the React dev server and the
 * production Nginx origin to communicate with the API.
 * In production, restrict {@code allowedOrigins} to your Azure Static Web App URL.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${FRONTEND_URL:http://localhost:5173}")
    private String frontendUrl;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns(
                        "http://localhost*",
                        "http://127.0.0.1*",
                        "https://*.azurestaticapps.net",
                        "https://*.azurewebsites.net",
                        "*"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
