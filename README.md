# 🏢 Enterprise API Test Automation Framework

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](https://jenkins.company.com/job/enterprise-api-tests)
[![Security Scan](https://img.shields.io/badge/security-A%2B-brightgreen)](https://security.company.com/scan/enterprise-api-tests)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

> **Enterprise-grade API test automation framework** with comprehensive CI/CD, cloud execution, and advanced reporting capabilities.

## 🎯 Overview

This framework provides a **complete enterprise solution** for API testing with:

- ✅ **Multi-Environment Support** (Local, Dev, QA, Staging, Prod)

- ✅ **Advanced CI/CD Pipeline** with Jenkins and quality gates
- ✅ **Comprehensive Reporting** (Allure, ExtentReports, Custom Interactive)


- ✅ **API Contract Testing** with OpenAPI/Swagger validation
- ✅ **Data-Driven Testing** with MongoDB, Excel, JSON, CSV support

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                    Enterprise Test Framework                    │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐            │
│  │   Jenkins   │  │   Security  │  │   Allure    │            │
│  │   Pipeline  │  │   Scanning  │  │   Reports   │            │
│  └─────────────┘  └─────────────┘  └─────────────┘            │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐            │
│  │   Docker    │  │   Cloud     │  │   Database  │            │
│  │ Container   │  │  Execution  │  │   Storage   │            │
│  └─────────────┘  └─────────────┘  └─────────────┘            │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐            │
│  │   MongoDB   │  │   Docker    │  │   Reports   │            │
│  │ Test Data   │  │ Container   │  │   Storage   │            │
│  └─────────────┘  └─────────────┘  └─────────────┘            │
└─────────────────────────────────────────────────────────────────┘
```

## 🚀 Quick Start

### Prerequisites

- **Java 17+** (OpenJDK or Oracle JDK)
- **Maven 3.9+**
- **Docker 20.10+**
- **Jenkins 2.375+** (for CI/CD)
- **MongoDB 6.0+** (for test data)

### Local Setup

```bash
# Clone the repository
git clone https://github.com/company/enterprise-api-test-framework.git
cd enterprise-api-test-framework

# Build the project
mvn clean install

# Run tests locally
mvn test

# Generate reports
./run-custom-report.sh
```

### Docker Setup

```bash
# Build Docker image
docker build -t enterprise-api-test-framework:latest .

# Run with Docker Compose
docker-compose up -d

# Run tests in container
docker run --rm enterprise-api-test-framework:latest mvn test
```

## 📋 Features

### 🔧 Core Framework

- **RestAssured Integration** - Powerful HTTP client for API testing
- **TestNG Framework** - Advanced test execution with parallel processing
- **Log4j2 Logging** - Structured logging with multiple appenders
- **Data Providers** - Support for Excel, JSON, CSV, MongoDB, and dynamic data
- **Configuration Management** - Multi-environment configuration support

### 🏢 Enterprise Features

#### 1. **Multi-Environment Support**
```java
// Environment-specific configuration
EnterpriseConfig config = EnterpriseConfig.getInstance();
Environment env = config.getEnvironment(); // LOCAL, DEV, QA, STAGING, PROD
```





#### 4. **API Contract Testing**
```java
@Test
@ContractTest
public void testAPIContract() {
    // OpenAPI/Swagger contract validation
    ContractValidator.validateAgainstSpec("api-spec.yaml");
}
```

### 📊 Reporting

#### 1. **Allure Reports**
- Interactive test reports with trends
- Screenshots and video attachments
- Environment information
- Test execution timeline

#### 2. **ExtentReports**
- Professional HTML reports
- Dashboard with metrics
- Test categorization
- Screenshot integration

#### 3. **Custom Interactive Reports**
- Real-time test execution
- Interactive charts and graphs
- API call details
- Performance metrics

### 🔄 CI/CD Pipeline

The Jenkins pipeline includes:

- **Code Quality** - Static code analysis
- **Security Scanning** - OWASP dependency check
- **Unit Tests** - Fast feedback loop
- **Integration Tests** - API contract validation


- **Docker Build** - Container image creation

- **Quality Gates** - Automated quality checks

## 📁 Project Structure

```
enterprise-api-test-framework/
├── src/
│   ├── main/java/com/enterprise/framework/
│   │   ├── api/                    # API service classes
│   │   ├── config/                 # Configuration management
│   │   ├── core/                   # Core framework components
│   │   ├── data/                   # Data providers
│   │   ├── enterprise/             # Enterprise features
│   │   ├── reporting/              # Reporting components
│   │   └── utils/                  # Utility classes
│   ├── test/java/com/enterprise/tests/
│   │   ├── ApiTests.java           # Generic API tests


│   │   └── ContractTests.java      # Contract tests
│   └── resources/
│       ├── config.properties       # Configuration files
│       ├── testdata/               # Test data files

├── docker/                        # Docker configuration
│   ├── Dockerfile
│   └── docker-compose.yml
├── jenkins/                       # Jenkins configuration
│   └── Jenkinsfile
├── docs/                          # Documentation
├── scripts/                       # Utility scripts
└── reports/                       # Generated reports
```

## 🔧 Configuration

### Environment Configuration

```properties
# config.properties
environment=local
base.url=https://api.company.com/v1
api.timeout=30000
api.retry.count=3

# Parallel execution
parallel.execution=true
thread.count=4

# Reporting
report.type=allure,extent,custom
report.path=target/reports

# Database
mongodb.uri=mongodb://localhost:27017
mongodb.database=enterprise_test




```

## 🧪 Test Examples

### Basic API Test
```java
@Test
public void testCreateResource() {
    Resource resource = new Resource();
    resource.setName("Test Resource");
    resource.setStatus("active");
    
    Resource createdResource = apiService.createResource(resource);
    
    Assert.assertNotNull(createdResource);
    Assert.assertEquals("Test Resource", createdResource.getName());
}
```

### Data-Driven Test
```java
@Test(dataProvider = "testData", dataProviderClass = DataProvider.class)
public void testCreateResourceWithData(Map<String, Object> testData) {
    Resource resource = Resource.fromMap(testData);
    Resource createdResource = apiService.createResource(resource);
    
    Assert.assertEquals(createdResource.getName(), testData.get("name"));
}
```







## 🚀 Deployment

### Local Development
```bash
# Run tests locally
mvn test

# Run specific test suite
mvn test -Dtest=SmokeTests

# Run with custom configuration
mvn test -Dconfig.file=custom-config.properties
```

### Docker Deployment
```bash
# Build and run with Docker Compose
docker-compose up -d

# Run tests in container
docker exec enterprise-tests mvn test
```

## 📈 Performance Optimization

### Parallel Execution
```xml
<!-- testng.xml -->
<suite name="Enterprise API Test Suite" parallel="methods" thread-count="4">
    <!-- Test configuration -->
</suite>
```

### Resource Management
```yaml
# Docker resource limits
resources:
  requests:
    memory: "1Gi"
    cpu: "500m"
  limits:
    memory: "2Gi"
    cpu: "1000m"
```

### Caching Strategy
```java
// Configuration caching
private final ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<>();

public String getProperty(String key, String defaultValue) {
    return cache.computeIfAbsent(key, k -> properties.getProperty(k, defaultValue)).toString();
}
```

## 🛠️ Troubleshooting

### Common Issues

1. **MongoDB Connection Issues**
   ```bash
   # Check MongoDB status
   docker ps | grep mongodb
   
   # Check connection string
   echo $MONGODB_URI
   ```

2. **Jenkins Pipeline Issues**
   ```bash
   # Check Jenkins logs
   docker logs jenkins
   
   # Check pipeline console output
   # Visit: http://jenkins:8080/job/enterprise-api-tests/
   ```

### Debug Mode
```bash
# Enable debug logging
mvn test -Dlog.level=DEBUG

# Run with verbose output
mvn test -X
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines

- Follow Java coding standards
- Add unit tests for new features
- Update documentation
- Run full test suite before submitting PR

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

- **Documentation**: [docs.company.com/enterprise-api-tests](https://docs.company.com/enterprise-api-tests)
- **Issues**: [GitHub Issues](https://github.com/company/enterprise-api-test-framework/issues)
- **Slack**: [#test-automation](https://company.slack.com/channels/test-automation)
- **Email**: qa-team@company.com

## 🙏 Acknowledgments

- **RestAssured** - HTTP client library
- **TestNG** - Testing framework
- **Allure** - Test reporting
- **ExtentReports** - HTML reporting
- **MongoDB** - Database
- **Docker** - Containerization
- **Jenkins** - CI/CD

---

**Made with ❤️ by the QA Team** 