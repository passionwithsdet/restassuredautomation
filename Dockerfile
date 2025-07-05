# Enterprise API Test Automation Framework Docker Image
# Multi-stage build for optimized image size

# Stage 1: Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS builder

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean compile test-compile -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:17-jre-jammy

# Install necessary packages
RUN apt-get update && apt-get install -y \
    curl \
    wget \
    unzip \
    git \
    && rm -rf /var/lib/apt/lists/*

# Set environment variables
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH=$JAVA_HOME/bin:$PATH
ENV MAVEN_HOME=/usr/share/maven
ENV PATH=$MAVEN_HOME/bin:$PATH

# Create app user
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Create necessary directories
RUN mkdir -p /app/target/reports \
    /app/target/logs \
    /app/target/screenshots \
    /app/target/allure-results \
    /app/target/allure-report \
    /app/test-results

# Set working directory
WORKDIR /app

# Copy built artifacts from builder stage
COPY --from=builder /app/target ./target
COPY --from=builder /app/src ./src
COPY --from=builder /app/pom.xml .

# Copy configuration files
COPY src/main/resources/config.properties ./src/main/resources/
COPY src/main/resources/log4j2.xml ./src/main/resources/
COPY testng.xml .

# Copy test data
COPY src/test/resources/testdata ./src/test/resources/testdata

# Set ownership
RUN chown -R appuser:appuser /app

# Switch to app user
USER appuser

# Expose ports (if needed for web reports)
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/health || exit 1

# Default command
CMD ["mvn", "clean", "test", "-Dtest=Smoke Tests"]

# Labels
LABEL maintainer="QA Team <ve.vinu@gmail.com>"
LABEL version="1.0.0"
LABEL description="Enterprise API Test Automation Framework"
LABEL framework="RestAssured + TestNG"
LABEL reporting="ExtentReports + Allure"
LABEL ci="Jenkins + Docker" 