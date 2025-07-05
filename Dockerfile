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
FROM maven:3.9.6-eclipse-temurin-17

# Install necessary packages
RUN apt-get update && apt-get install -y \
    curl \
    wget \
    unzip \
    git \
    && rm -rf /var/lib/apt/lists/*

# Install Allure command-line tool
RUN curl -o allure-2.24.1.tgz -Ls https://repo.maven.apache.org/maven2/io/qameta/allure/allure-commandline/2.24.1/allure-commandline-2.24.1.tgz \
    && tar -zxvf allure-2.24.1.tgz -C /opt/ \
    && ln -s /opt/allure-2.24.1/bin/allure /usr/local/bin/allure \
    && rm allure-2.24.1.tgz

# Set environment variables
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH=$JAVA_HOME/bin:$PATH
ENV MAVEN_HOME=/usr/share/maven
ENV PATH=$MAVEN_HOME/bin:$PATH

# Create app user
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Create necessary directories with proper permissions
RUN mkdir -p /app/target/reports \
    /app/target/logs \
    /app/target/screenshots \
    /app/target/allure-results \
    /app/target/allure-report \
    /app/test-results \
    /home/appuser/.m2/repository

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

# Set ownership and permissions
RUN chown -R appuser:appuser /app /home/appuser && \
    chmod -R 755 /app/target && \
    chmod -R 777 /app/target/reports && \
    chmod -R 777 /app/target/logs && \
    chmod -R 777 /app/target/screenshots && \
    chmod -R 777 /app/target/allure-results && \
    chmod -R 777 /app/target/allure-report

# Create a startup script to handle permissions
RUN echo '#!/bin/bash\n\
if [ "$(id -u)" = "0" ]; then\n\
    # If running as root, fix permissions and switch to appuser\n\
    chown -R appuser:appuser /app/target\n\
    chmod -R 755 /app/target\n\
    chmod -R 777 /app/target/reports\n\
    chmod -R 777 /app/target/logs\n\
    chmod -R 777 /app/target/screenshots\n\
    chmod -R 777 /app/target/allure-results\n\
    chmod -R 777 /app/target/allure-report\n\
    exec gosu appuser "$@"\n\
else\n\
    # If not root, run directly\n\
    exec "$@"\n\
fi' > /usr/local/bin/entrypoint.sh && \
    chmod +x /usr/local/bin/entrypoint.sh

# Install gosu for proper user switching
RUN apt-get update && apt-get install -y gosu && rm -rf /var/lib/apt/lists/*

# Don't switch to appuser here - let entrypoint handle it
# USER appuser

# Set entrypoint
ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]

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