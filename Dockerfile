# =======================================================
# BloodConnect - Production Dockerfile for Cloud (Render / Railway)
# Multi-stage build for optimal image size and speed
# =======================================================

# Stage 1: Build JAR with Maven & Eclipse Temurin JDK 17
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build production jar skipping tests with heap restriction to fit free tier limits
ENV MAVEN_OPTS="-Xmx384m"
RUN mvn clean package -DskipTests -B

# Stage 2: Minimal runtime image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Add unprivileged user for security
RUN addgroup -S bloodgroup && adduser -S blooduser -G bloodgroup
USER blooduser:bloodgroup

# Copy compiled jar from builder stage
COPY --from=builder /app/target/bloodconnect-1.0.0.jar app.jar

# Render and Railway default port
ENV PORT=8080
EXPOSE 8080

# Run Spring Boot with container memory optimization
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
