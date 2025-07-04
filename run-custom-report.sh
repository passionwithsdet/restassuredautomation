#!/bin/bash

# PetStore API Test Framework - Custom Report Runner
# This script runs the tests and generates custom HTML reports

echo "🐾 PetStore API Test Framework - Custom Report Generator"
echo "========================================================"

# Set environment variables
export MAVEN_OPTS="-Xmx2g"

# Clean and compile
echo "📦 Cleaning and compiling project..."
mvn clean compile test-compile

if [ $? -ne 0 ]; then
    echo "❌ Compilation failed!"
    exit 1
fi

# Run tests with custom reporting
echo "🧪 Running tests with custom reporting..."
mvn test -Dtest=CustomReportExampleTest

if [ $? -ne 0 ]; then
    echo "❌ Tests failed!"
    exit 1
fi

# Check for generated reports
echo "📊 Checking for generated reports..."
REPORT_DIR="target/reports"
CUSTOM_REPORT=$(find $REPORT_DIR -name "petstore_api_report_*.html" -type f | head -1)

if [ -n "$CUSTOM_REPORT" ]; then
    echo "✅ Custom report generated: $CUSTOM_REPORT"
    echo "🌐 Opening report in browser..."
    
    # Try to open the report in default browser
    if command -v open >/dev/null 2>&1; then
        # macOS
        open "$CUSTOM_REPORT"
    elif command -v xdg-open >/dev/null 2>&1; then
        # Linux
        xdg-open "$CUSTOM_REPORT"
    elif command -v start >/dev/null 2>&1; then
        # Windows
        start "$CUSTOM_REPORT"
    else
        echo "📄 Report location: $CUSTOM_REPORT"
        echo "Please open the report manually in your browser"
    fi
else
    echo "❌ Custom report not found!"
    echo "📁 Checking report directory: $REPORT_DIR"
    ls -la "$REPORT_DIR" 2>/dev/null || echo "Report directory does not exist"
fi

echo ""
echo "🎉 Custom report generation completed!"
echo "📁 Reports are located in: $REPORT_DIR" 