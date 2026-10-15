# ─── Stage 1: Build ──────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copy Maven wrapper and POM first for layer-cached dependency resolution
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -q

# Copy source and build fat JAR (skip tests — run them in CI pipeline)
COPY src ./src
RUN ./mvnw package -DskipTests -q

# ─── Stage 2: Runtime ────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime

# Create non-root user for security hardening
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
WORKDIR /app

# Copy only the fat JAR from builder
COPY --from=builder /app/target/*.jar app.jar

# Azure App Service / ACI uses PORT env variable
ENV PORT=8080
EXPOSE ${PORT}

USER appuser

# JVM flags optimized for containers: respects cgroup memory limits
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-XX:+ExitOnOutOfMemoryError", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
