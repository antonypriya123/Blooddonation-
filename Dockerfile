# =======================================================
# BloodConnect - Production Dockerfile for Railway Cloud
# Multi-stage build for optimal image size and speed
# =======================================================

# Stage 1: Build JAR with Maven & Eclipse Temurin JDK 17
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and package
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Minimal runtime image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Add unprivileged user for security
RUN addgroup -S bloodgroup && adduser -S blooduser -G bloodgroup
USER blooduser:bloodgroup

# Copy compiled jar from builder stage
COPY --from=builder /app/target/bloodconnect-1.0.0.jar app.jar

# Railway injects $PORT environment variable
ENV PORT=8080
EXPOSE 8080

# Run Spring Boot application
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
