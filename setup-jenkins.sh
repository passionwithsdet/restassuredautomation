#!/bin/bash

# 🚀 Jenkins Setup Script for Enterprise API Test Framework
# This script helps set up Jenkins for your local development environment

set -e

echo "🚀 Setting up Jenkins for Enterprise API Test Framework..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if Homebrew is installed
check_homebrew() {
    print_status "Checking Homebrew installation..."
    if ! command -v brew &> /dev/null; then
        print_error "Homebrew is not installed. Please install it first:"
        echo "  /bin/bash -c \"\$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)\""
        exit 1
    fi
    print_success "Homebrew is installed"
}

# Install Jenkins
install_jenkins() {
    print_status "Installing Jenkins LTS..."
    if brew list jenkins-lts &> /dev/null; then
        print_warning "Jenkins LTS is already installed"
    else
        brew install jenkins-lts
        print_success "Jenkins LTS installed successfully"
    fi
}

# Start Jenkins
start_jenkins() {
    print_status "Starting Jenkins service..."
    brew services start jenkins-lts
    
    # Wait for Jenkins to start
    print_status "Waiting for Jenkins to start..."
    sleep 10
    
    # Check if Jenkins is running
    if curl -s http://localhost:8080 &> /dev/null; then
        print_success "Jenkins is running on http://localhost:8080"
    else
        print_warning "Jenkins might still be starting. Please check http://localhost:8080"
    fi
}

# Get initial admin password
get_admin_password() {
    print_status "Getting initial admin password..."
    if [ -f ~/Library/Application\ Support/Jenkins/secrets/initialAdminPassword ]; then
        ADMIN_PASSWORD=$(cat ~/Library/Application\ Support/Jenkins/secrets/initialAdminPassword)
        print_success "Initial admin password: $ADMIN_PASSWORD"
        echo ""
        echo "🔑 Use this password to unlock Jenkins:"
        echo "   $ADMIN_PASSWORD"
        echo ""
    else
        print_warning "Could not find initial admin password file"
        print_warning "Please check Jenkins logs or restart Jenkins"
    fi
}

# Install required tools
install_tools() {
    print_status "Checking required tools..."
    
    # Check Java
    if ! command -v java &> /dev/null; then
        print_warning "Java not found. Installing OpenJDK 17..."
        brew install openjdk@17
        echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
        source ~/.zshrc
    else
        print_success "Java is installed: $(java -version 2>&1 | head -n 1)"
    fi
    
    # Check Maven
    if ! command -v mvn &> /dev/null; then
        print_warning "Maven not found. Installing Maven..."
        brew install maven
    else
        print_success "Maven is installed: $(mvn -version 2>&1 | head -n 1)"
    fi
    
    # Check Docker
    if ! command -v docker &> /dev/null; then
        print_warning "Docker not found. Please install Docker Desktop from https://www.docker.com/products/docker-desktop"
    else
        print_success "Docker is installed: $(docker --version)"
    fi
}

# Setup Git repository
setup_git() {
    print_status "Setting up Git repository..."
    
    if [ ! -d .git ]; then
        print_status "Initializing Git repository..."
        git init
        git add .
        git commit -m "Initial commit: Enterprise API Test Framework"
        print_success "Git repository initialized"
    else
        print_success "Git repository already exists"
    fi
    
    # Check if remote exists
    if ! git remote get-url origin &> /dev/null; then
        print_warning "No remote repository configured"
        echo "To add a remote repository, run:"
        echo "  git remote add origin https://github.com/your-username/enterprise-api-test-framework.git"
        echo "  git push -u origin main"
    else
        print_success "Remote repository configured: $(git remote get-url origin)"
    fi
}

# Create Jenkins configuration
create_jenkins_config() {
    print_status "Creating Jenkins configuration files..."
    
    # Create jenkins-config.properties
    cat > jenkins-config.properties << EOF
# Jenkins-specific configuration
environment=jenkins
base.url=https://petstore.swagger.io/v2
timeout=60
retry.count=3
parallel.execution=true
thread.count=4
report.path=target/reports
allure.results.directory=target/allure-results
log.level=INFO
EOF
    
    print_success "Created jenkins-config.properties"
}

# Display next steps
show_next_steps() {
    echo ""
    echo "🎯 Next Steps:"
    echo "=============="
    echo ""
    echo "1. 🌐 Access Jenkins:"
    echo "   http://localhost:8080"
    echo ""
    echo "2. 🔑 Unlock Jenkins:"
    echo "   Use the admin password shown above"
    echo ""
    echo "3. 📦 Install Jenkins Plugins:"
    echo "   - Git Integration"
    echo "   - Pipeline"
    echo "   - Maven Integration"
    echo "   - JDK Tool"
    echo "   - Allure Jenkins Plugin"
    echo "   - HTML Publisher"
    echo "   - Test Results Aggregator"
    echo "   - Timestamper"
    echo "   - AnsiColor"
    echo ""
    echo "4. ⚙️ Configure Jenkins Tools:"
    echo "   - JDK-17: /opt/homebrew/Cellar/openjdk/17.0.9/libexec/openjdk.jdk/Contents/Home"
    echo "   - Maven-3.9: /opt/homebrew/Cellar/maven/3.9.5/libexec"
    echo ""
    echo "5. 🏗️ Create Jenkins Pipeline Job:"
    echo "   - Name: enterprise-api-test-framework"
    echo "   - Type: Pipeline"
    echo "   - Script Path: Jenkinsfile.local"
    echo ""
    echo "6. 🔄 Configure Git Integration:"
    echo "   - Repository URL: Your Git repository URL"
    echo "   - Credentials: Your Git credentials"
    echo ""
    echo "📚 For detailed instructions, see: jenkins-setup-guide.md"
    echo ""
}

# Main execution
main() {
    echo "🚀 Jenkins Setup for Enterprise API Test Framework"
    echo "=================================================="
    echo ""
    
    check_homebrew
    install_tools
    install_jenkins
    start_jenkins
    get_admin_password
    setup_git
    create_jenkins_config
    
    print_success "Setup completed successfully!"
    show_next_steps
}

# Run main function
main "$@" 