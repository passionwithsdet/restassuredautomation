# 🚀 Jenkins Setup Guide for Enterprise API Test Framework

## 📋 Prerequisites

### 1. **Jenkins Installation**
```bash
# Install Jenkins on macOS
brew install jenkins-lts

# Start Jenkins
brew services start jenkins-lts

# Access Jenkins
open http://localhost:8080
```

### 2. **Required Jenkins Plugins**
Install these plugins via **Manage Jenkins > Manage Plugins**:

#### **Core Plugins:**
- ✅ **Git Integration** - For Git repository access
- ✅ **Pipeline** - For Jenkinsfile support
- ✅ **Maven Integration** - For Maven builds
- ✅ **JDK Tool** - For Java builds
- ✅ **Allure Jenkins Plugin** - For test reporting
- ✅ **HTML Publisher** - For HTML reports
- ✅ **Test Results Aggregator** - For test results
- ✅ **Timestamper** - For build timestamps
- ✅ **AnsiColor** - For colored console output

#### **Optional Plugins:**
- ✅ **Slack Notification** - For Slack integration
- ✅ **Email Extension** - For email notifications
- ✅ **Docker Pipeline** - For Docker integration
- ✅ **Credentials Binding** - For secure credentials
- ✅ **Blue Ocean** - For modern UI

## 🔧 Jenkins Configuration

### 1. **Global Tool Configuration**
Go to **Manage Jenkins > Global Tool Configuration**:

#### **JDK Configuration:**
```
Name: JDK-17
JAVA_HOME: /opt/homebrew/Cellar/openjdk/17.0.9/libexec/openjdk.jdk/Contents/Home
```

#### **Maven Configuration:**
```
Name: Maven-3.9
MAVEN_HOME: /opt/homebrew/Cellar/maven/3.9.5/libexec
```

### 2. **Credentials Setup**
Go to **Manage Jenkins > Manage Credentials > System > Global credentials**:

#### **Git Credentials:**
```
Kind: Username with password
Scope: Global
Username: your-git-username
Password: your-git-password
ID: git-credentials
Description: Git repository credentials
```

#### **Docker Registry Credentials (NOT NEEDED):**
```
No Docker credentials required! Jenkins handles Docker automatically.
Only Git/SCM credentials are needed for code checkout.
```

## 🏗️ Jenkins Job Setup

### 1. **Create New Pipeline Job**

1. **Go to Jenkins Dashboard**
2. **Click "New Item"**
3. **Enter job name:** `enterprise-api-test-framework`
4. **Select "Pipeline"**
5. **Click "OK"**

### 2. **Pipeline Configuration**

#### **General Settings:**
```
✅ Discard old builds
   - Keep only the last 10 builds
   
✅ This project is parameterized
   - String Parameter: TEST_ENVIRONMENT (default: local)
   - Choice Parameter: TEST_SUITE (choices: smoke, regression, integration)
   - Boolean Parameter: RUN_PARALLEL (default: true)
```

#### **Build Triggers:**
```
✅ Poll SCM
   - Schedule: H/15 * * * * (every 15 minutes)

✅ GitHub hook trigger for GITScm polling
```

#### **Pipeline Definition:**
```
Definition: Pipeline script from SCM
SCM: Git
Repository URL: https://github.com/your-username/enterprise-api-test-framework.git
Credentials: git-credentials
Branch Specifier: */main
Script Path: Jenkinsfile
```

### 3. **Advanced Pipeline Configuration**

#### **Environment Variables:**
```groovy
environment {
    JAVA_HOME = tool 'JDK-17'
    MAVEN_HOME = tool 'Maven-3.9'
    TEST_ENVIRONMENT = 'jenkins'
    PARALLEL_THREADS = '4'
    REPORT_PATH = 'target/reports'
    ALLURE_RESULTS = 'target/allure-results'
}
```

## 🔄 Git Repository Setup

### 1. **Initialize Git Repository**
```bash
# Initialize Git repository
git init

# Add all files
git add .

# Initial commit
git commit -m "Initial commit: Enterprise API Test Framework"

# Add remote repository
git remote add origin https://github.com/your-username/enterprise-api-test-framework.git

# Push to main branch
git push -u origin main
```

### 2. **Git Hooks (Optional)**
Create `.git/hooks/pre-commit`:
```bash
#!/bin/bash
# Run tests before commit
mvn clean test -Dtest=SmokeTests
if [ $? -ne 0 ]; then
    echo "Tests failed. Commit aborted."
    exit 1
fi
```

### 3. **Branch Strategy**
```bash
# Create development branch
git checkout -b develop
git push -u origin develop

# Create feature branch
git checkout -b feature/new-test-suite
# ... make changes ...
git commit -m "Add new test suite"
git push origin feature/new-test-suite
```

## 📊 Pipeline Stages Overview

### **Stage 1: Checkout & Setup**
- ✅ Checkout code from Git
- ✅ Validate environment (Java, Maven, Docker)
- ✅ Setup workspace directories

### **Stage 2: Code Quality**
- ✅ Static code analysis
- ✅ Code coverage analysis
- ✅ Security scanning

### **Stage 3: Build & Test**
- ✅ **Unit Tests** - Fast feedback loop
- ✅ **Integration Tests** - API integration validation
- ✅ **API Contract Tests** - Contract validation

### **Stage 4: Build Docker Image**
- ✅ Build Docker image with test framework
- ✅ Tag and version management

### **Stage 5: Push to Registry**
- ✅ Push to Docker registry (main/develop branches only)

### **Stage 6: Quality Gates**
- ✅ Test coverage validation (80% threshold)
- ✅ Test result validation

## 📈 Reporting & Notifications

### 1. **Test Reports**
- **Allure Reports**: Interactive test reports
- **ExtentReports**: HTML test reports
- **Surefire Reports**: JUnit XML reports
- **Custom Reports**: Enterprise-specific reports

### 2. **Notifications**
- **Email**: Build status notifications
- **Slack**: Real-time notifications
- **Webhooks**: Custom integrations

### 3. **Artifacts**
- Test reports and logs
- Docker images
- Build artifacts
- Coverage reports

## 🚀 Quick Start Commands

### **Local Development:**
```bash
# Run tests locally
mvn clean test

# Run specific test suite
mvn test -Dtest=SmokeTests

# Run with custom configuration
mvn test -Dconfig.file=jenkins-config.properties
```

### **Jenkins Pipeline:**
```bash
# Trigger build manually
curl -X POST http://localhost:8080/job/enterprise-api-test-framework/build

# Trigger with parameters
curl -X POST "http://localhost:8080/job/enterprise-api-test-framework/buildWithParameters?TEST_ENVIRONMENT=staging&TEST_SUITE=regression"
```

### **Docker Execution:**
```bash
# Build Docker image
docker build -t enterprise-api-test-framework .

# Run tests in container
docker run --rm enterprise-api-test-framework mvn test
```

## 🔧 Troubleshooting

### **Common Issues:**

1. **Git Authentication:**
   ```bash
   # Generate SSH key
   ssh-keygen -t rsa -b 4096 -C "your-email@example.com"
   
   # Add to GitHub
   cat ~/.ssh/id_rsa.pub
   ```

2. **Maven Dependencies:**
   ```bash
   # Clear Maven cache
   mvn dependency:purge-local-repository
   
   # Update dependencies
   mvn versions:use-latest-versions
   ```

3. **Jenkins Permissions:**
   ```bash
   # Fix Jenkins permissions
   sudo chown -R jenkins:jenkins /var/lib/jenkins
   ```

4. **Docker Issues:**
   ```bash
   # Clean Docker system
   docker system prune -a
   
   # Restart Docker
   sudo systemctl restart docker
   ```

## 📞 Support

- **Jenkins Documentation**: https://jenkins.io/doc/
- **Pipeline Syntax**: https://jenkins.io/doc/book/pipeline/syntax/
- **Git Integration**: https://plugins.jenkins.io/git/
- **Allure Reports**: https://docs.qameta.io/allure/

---

**🎯 Your Enterprise API Test Framework is now ready for CI/CD!** 