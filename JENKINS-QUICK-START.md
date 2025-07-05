# 🚀 Jenkins Quick Start Guide

## ⚡ Quick Setup (5 minutes)

### 1. **Run Setup Script**
```bash
./setup-jenkins.sh
```

### 2. **Access Jenkins**
- Open: http://localhost:8080
- Use admin password from setup script

### 3. **Install Required Plugins**
Go to **Manage Jenkins > Manage Plugins > Available**:
- ✅ Git Integration
- ✅ Pipeline
- ✅ Maven Integration
- ✅ JDK Tool
- ✅ Allure Jenkins Plugin
- ✅ HTML Publisher
- ✅ Test Results Aggregator
- ✅ Timestamper
- ✅ AnsiColor

### 4. **Configure Tools**
Go to **Manage Jenkins > Global Tool Configuration**:

**JDK:**
```
Name: JDK-17
JAVA_HOME: /opt/homebrew/Cellar/openjdk/17.0.9/libexec/openjdk.jdk/Contents/Home
```

**Maven:**
```
Name: Maven-3.9
MAVEN_HOME: /opt/homebrew/Cellar/maven/3.9.5/libexec
```

### 5. **Create Pipeline Job**
1. **New Item** → **Pipeline** → **enterprise-api-test-framework**
2. **Pipeline Definition:**
   ```
   Definition: Pipeline script from SCM
   SCM: Git
   Repository URL: [Your Git repo URL]
   Script Path: Jenkinsfile.local
   ```

### 6. **Run First Build**
- Click **Build Now**
- Check console output
- View test results and reports

## 🔧 Manual Setup (Alternative)

### Install Jenkins
```bash
# Install Jenkins LTS
brew install jenkins-lts

# Start Jenkins
brew services start jenkins-lts

# Access Jenkins
open http://localhost:8080
```

### Setup Git Repository
```bash
# Initialize Git
git init
git add .
git commit -m "Initial commit"

# Add remote (replace with your repo)
git remote add origin https://github.com/your-username/enterprise-api-test-framework.git
git push -u origin main
```

## 📊 Pipeline Stages

1. **Checkout & Setup** - Clone code, validate environment
2. **Build & Test** - Run unit and integration tests
3. **Build Docker Image** - Create container image
4. **Quality Gates** - Validate test results

## 📈 Reports & Artifacts

- **Allure Reports**: Interactive test reports
- **Test Results**: JUnit XML reports
- **Build Artifacts**: Logs, reports, Docker images
- **Console Output**: Detailed build logs

## 🚀 Common Commands

### Local Testing
```bash
# Run all tests
mvn clean test

# Run specific test suite
mvn test -Dtest=PetStoreApiTests

# Run with Jenkins config
mvn test -Dconfig.file=jenkins-config.properties
```

### Jenkins API
```bash
# Trigger build
curl -X POST http://localhost:8080/job/enterprise-api-test-framework/build

# Trigger with parameters
curl -X POST "http://localhost:8080/job/enterprise-api-test-framework/buildWithParameters?TEST_ENVIRONMENT=staging"
```

### Docker
```bash
# Build image
docker build -t enterprise-api-test-framework .

# Run tests in container
docker run --rm enterprise-api-test-framework mvn test
```

## 🔍 Troubleshooting

### Jenkins Not Starting
```bash
# Check Jenkins status
brew services list | grep jenkins

# Restart Jenkins
brew services restart jenkins-lts

# Check logs
tail -f ~/Library/Logs/Jenkins/jenkins.log
```

### Build Failures
```bash
# Check Java version
java -version

# Check Maven version
mvn -version

# Clear Maven cache
mvn dependency:purge-local-repository
```

### Git Issues
```bash
# Check Git configuration
git config --list

# Test Git connection
git ls-remote origin
```

## 📞 Support

- **Jenkins Documentation**: https://jenkins.io/doc/
- **Pipeline Syntax**: https://jenkins.io/doc/book/pipeline/syntax/
- **Allure Reports**: https://docs.qameta.io/allure/

---

**🎯 Your Enterprise API Test Framework is ready for CI/CD!** 