# 🐳 Jenkins with Docker Execution Guide

## 🎯 Overview

This guide shows you how to run your Enterprise API Test Framework tests in Docker containers from Jenkins, providing:

- ✅ **Consistent Environment** - Same test environment across all builds
- ✅ **Isolation** - Tests run in isolated containers
- ✅ **Scalability** - Easy to scale with multiple containers
- ✅ **Reproducibility** - Guaranteed same environment every time
- ✅ **Resource Management** - Better resource utilization

## 🚀 Quick Start (3 Options)

### Option 1: Simple Docker Execution
```bash
# Use Jenkinsfile.docker
# Script Path: Jenkinsfile.docker
```

### Option 2: Docker Compose Execution (Recommended)
```bash
# Use Jenkinsfile.docker-compose
# Script Path: Jenkinsfile.docker-compose
```

### Option 3: Local Jenkins Setup
```bash
# Use Jenkinsfile.local
# Script Path: Jenkinsfile.local
```

## 📁 Docker Files Overview

### **Dockerfile.jenkins**
- Optimized for Jenkins execution
- Multi-stage build for smaller image size
- Pre-configured for volume mounts
- Security with non-root user

### **docker-compose.jenkins.yml**
- Complete test environment
- MongoDB for test data
- Allure report server
- Network isolation

### **Jenkinsfile.docker**
- Direct Docker container execution
- Volume mounts for reports
- Parallel test execution
- Resource cleanup

### **Jenkinsfile.docker-compose**
- Docker Compose orchestration
- Service dependencies
- Automatic cleanup
- Allure server integration

## 🔧 Jenkins Configuration

### 1. **Prerequisites**
```bash
# Install Docker
brew install --cask docker

# Install Docker Compose
brew install docker-compose

# Start Docker Desktop
open /Applications/Docker.app
```

### 2. **Jenkins Plugins**
Install these additional plugins:
- ✅ **Docker Pipeline** - Docker integration
- ✅ **Docker plugin** - Docker support
- ✅ **Docker Compose** - Compose support

### 3. **Jenkins Job Setup**

#### **Pipeline Configuration:**
```
Definition: Pipeline script from SCM
SCM: Git
Repository URL: [Your Git repo URL]
Script Path: Jenkinsfile.docker-compose  # or Jenkinsfile.docker
```

#### **Environment Variables:**
```groovy
environment {
    DOCKER_IMAGE = 'enterprise-api-test-framework'
    DOCKER_TAG = "${env.BUILD_NUMBER}"
    TEST_ENVIRONMENT = 'jenkins'
    PARALLEL_THREADS = '4'
}
```

## 🐳 Docker Execution Options

### **Option 1: Direct Docker Commands**
```groovy
// Jenkinsfile.docker
docker run --rm \
    -v ${WORKSPACE}:/workspace \
    -v ${WORKSPACE}/reports:/app/target/reports \
    enterprise-api-test-framework:${BUILD_NUMBER} \
    mvn clean test
```

### **Option 2: Docker Compose**
```groovy
// Jenkinsfile.docker-compose
docker-compose -f docker-compose.jenkins.yml run --rm \
    -e TEST_SUITE=unit \
    test-runner \
    mvn clean test
```

### **Option 3: Parallel Docker Execution**
```groovy
parallel {
    stage('Unit Tests') {
        steps {
            sh '''
                docker run --rm \
                    --name test-unit-${BUILD_NUMBER} \
                    -v ${WORKSPACE}/reports:/app/target/reports \
                    enterprise-api-test-framework:${BUILD_NUMBER} \
                    mvn test -Dtest=**/*Test
            '''
        }
    }
    stage('Integration Tests') {
        steps {
            sh '''
                docker run --rm \
                    --name test-integration-${BUILD_NUMBER} \
                    -v ${WORKSPACE}/reports:/app/target/reports \
                    enterprise-api-test-framework:${BUILD_NUMBER} \
                    mvn test -Dtest=**/*IntegrationTest
            '''
        }
    }
}
```

## 📊 Volume Mounts

### **Essential Mounts:**
```bash
# Workspace mount
-v ${WORKSPACE}:/workspace

# Reports mount
-v ${WORKSPACE}/reports:/app/target/reports

# Logs mount
-v ${WORKSPACE}/logs:/app/target/logs

# Allure results mount
-v ${WORKSPACE}/allure-results:/app/target/allure-results
```

### **Docker Compose Volumes:**
```yaml
volumes:
  - ${WORKSPACE}:/workspace:rw
  - ${WORKSPACE}/reports:/app/target/reports:rw
  - ${WORKSPACE}/logs:/app/target/logs:rw
  - ${WORKSPACE}/allure-results:/app/target/allure-results:rw
```

## 🔄 Pipeline Stages

### **Docker Execution Pipeline:**
1. **Checkout & Setup** - Clone code, setup directories
2. **Build Docker Image** - Build test framework image
3. **Start Test Environment** - Start MongoDB, services
4. **Run Tests in Docker** - Execute tests in containers
5. **Generate Reports** - Create custom reports
6. **Start Allure Server** - Launch report server
7. **Quality Gates** - Validate results

### **Parallel Execution:**
```groovy
stage('Run Tests in Docker') {
    parallel {
        stage('Unit Tests') { /* Docker unit tests */ }
        stage('Integration Tests') { /* Docker integration tests */ }
        stage('API Contract Tests') { /* Docker contract tests */ }
    }
}
```

## 📈 Reporting & Artifacts

### **Docker Report Collection:**
```groovy
post {
    always {
        // Archive reports from Docker containers
        archiveArtifacts artifacts: '**/reports/**/*, **/logs/**/*, **/allure-results/**/*'
        
        // Generate Allure report
        allure([
            results: [[path: 'allure-results']]
        ])
    }
}
```

### **Allure Server:**
```yaml
# docker-compose.jenkins.yml
allure-server:
  image: frankescobar/allure-docker-service
  ports:
    - "5050:5050"
  volumes:
    - ${WORKSPACE}/allure-results:/app/allure-results:ro
```

## 🚀 Quick Commands

### **Local Docker Testing:**
```bash
# Build image
docker build -f Dockerfile.jenkins -t enterprise-api-test-framework .

# Run tests
docker run --rm enterprise-api-test-framework mvn test

# Run with volume mounts
docker run --rm \
    -v $(pwd)/reports:/app/target/reports \
    enterprise-api-test-framework mvn test
```

### **Docker Compose Testing:**
```bash
# Build and run
docker-compose -f docker-compose.jenkins.yml up --build

# Run specific service
docker-compose -f docker-compose.jenkins.yml run --rm test-runner mvn test

# Cleanup
docker-compose -f docker-compose.jenkins.yml down -v
```

### **Jenkins API:**
```bash
# Trigger Docker build
curl -X POST http://localhost:8080/job/enterprise-api-test-framework/build

# Trigger with parameters
curl -X POST "http://localhost:8080/job/enterprise-api-test-framework/buildWithParameters?TEST_ENVIRONMENT=docker"
```

## 🔍 Troubleshooting

### **Docker Issues:**
```bash
# Check Docker status
docker info

# Check running containers
docker ps

# Check container logs
docker logs <container-name>

# Cleanup Docker
docker system prune -a
```

### **Volume Mount Issues:**
```bash
# Check volume mounts
docker inspect <container-name> | grep -A 10 "Mounts"

# Test volume access
docker run --rm -v $(pwd):/workspace alpine ls /workspace
```

### **Permission Issues:**
```bash
# Fix file permissions
chmod -R 755 ${WORKSPACE}/reports
chmod -R 755 ${WORKSPACE}/logs

# Run as specific user
docker run --rm -u $(id -u):$(id -g) enterprise-api-test-framework mvn test
```

### **Network Issues:**
```bash
# Check Docker networks
docker network ls

# Inspect network
docker network inspect jenkins-test-network

# Connect to network
docker run --rm --network jenkins-test-network enterprise-api-test-framework mvn test
```

## 📊 Performance Optimization

### **Docker Optimization:**
```dockerfile
# Multi-stage build
FROM maven:3.9.5-openjdk-17 AS builder
# ... build stage

FROM openjdk:17-jre-slim
# ... runtime stage
```

### **Resource Limits:**
```yaml
# docker-compose.jenkins.yml
services:
  test-runner:
    deploy:
      resources:
        limits:
          memory: 2G
          cpus: '2'
        reservations:
          memory: 1G
          cpus: '1'
```

### **Parallel Execution:**
```groovy
// Jenkinsfile.docker
parallel {
    stage('Unit Tests') { /* 4 threads */ }
    stage('Integration Tests') { /* 2 threads */ }
    stage('Contract Tests') { /* 2 threads */ }
}
```

## 🎯 Best Practices

### **Docker Best Practices:**
1. **Use multi-stage builds** for smaller images
2. **Run as non-root user** for security
3. **Use specific image tags** for reproducibility
4. **Clean up resources** after execution
5. **Use volume mounts** for persistent data

### **Jenkins Best Practices:**
1. **Use Docker agents** for consistency
2. **Implement proper cleanup** in post actions
3. **Archive artifacts** from containers
4. **Use parallel execution** for efficiency
5. **Monitor resource usage**

### **Test Execution Best Practices:**
1. **Isolate test environments** per container
2. **Use environment variables** for configuration
3. **Implement proper logging** in containers
4. **Handle test failures** gracefully
5. **Generate comprehensive reports**

## 📞 Support

- **Docker Documentation**: https://docs.docker.com/
- **Docker Compose**: https://docs.docker.com/compose/
- **Jenkins Docker Plugin**: https://plugins.jenkins.io/docker/
- **Allure Docker**: https://docs.qameta.io/allure/#_docker

---

**🐳 Your Enterprise API Test Framework is now ready for Docker-powered CI/CD!** 