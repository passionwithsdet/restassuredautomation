# 🔐 Credentials Setup Guide

## ✅ **Only Git/SCM Credentials Required**

### **What You Need:**
- ✅ **Git Credentials** - For code checkout from repository
- ❌ **Docker Credentials** - NOT needed (Jenkins handles Docker automatically)
- ❌ **Registry Credentials** - NOT needed (using local Docker images)

## 🔧 **Git Credentials Setup**

### **Step 1: Go to Jenkins Credentials**
1. **Manage Jenkins** → **Manage Credentials**
2. **System** → **Global credentials (unrestricted)**
3. **Add Credentials**

### **Step 2: Configure Git Credentials**
```
Kind: Username with password
Scope: Global
Username: your-git-username
Password: your-git-password (or personal access token)
ID: git-credentials
Description: Git repository credentials
```

### **Step 3: Use in Pipeline**
```groovy
// Jenkinsfile - No credentials needed in code
pipeline {
    agent any
    
    stages {
        stage('Checkout') {
            steps {
                // Jenkins uses Git credentials automatically
                checkout scm
            }
        }
    }
}
```

## 🐳 **Docker - No Credentials Needed**

### **Why No Docker Credentials?**
- ✅ **Local Docker Images** - Built from source code
- ✅ **No Registry Push** - Images stay local
- ✅ **Jenkins Handles Docker** - Automatic Docker execution
- ✅ **No External Registry** - Everything runs locally

### **Docker Execution Flow:**
```bash
# Jenkins automatically runs these commands:
docker-compose -f docker-compose.jenkins.yml build test-runner
docker-compose -f docker-compose.jenkins.yml run --rm test-runner mvn test
```

## 📋 **Complete Setup Checklist**

### **Required:**
- ✅ Jenkins LTS installed
- ✅ Git credentials configured
- ✅ Required Jenkins plugins installed
- ✅ Git repository access

### **NOT Required:**
- ❌ Docker Desktop installation
- ❌ Docker credentials
- ❌ Docker registry credentials
- ❌ External Docker registry
- ❌ Manual Docker setup

## 🚀 **Quick Verification**

### **Test Git Access:**
```bash
# Test if Jenkins can access your Git repository
# This should work with your Git credentials
git clone https://github.com/your-username/enterprise-api-test-framework.git
```

### **Test Docker (Optional):**
```bash
# Only needed if you want to test locally
docker --version
docker-compose --version
```

## 🎯 **Summary**

**✅ Only Git credentials are required!**

1. **Configure Git credentials** in Jenkins
2. **Jenkins pulls code** from Git repository
3. **Jenkins builds Docker image** locally
4. **Jenkins runs tests** in Docker containers
5. **Jenkins collects results** and reports

**No Docker credentials, registry access, or external Docker setup needed!**

---

**🔐 Your setup is secure and simple - only Git access required!** 