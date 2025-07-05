package com.petstore.tests;

import com.petstore.framework.reporting.CustomReportGenerator;
import com.petstore.framework.reporting.CustomReportUtility;
import org.testng.annotations.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Test class to manually generate custom report
 */
public class GenerateCustomReportTest {
    private static final Logger logger = LogManager.getLogger(GenerateCustomReportTest.class);
    
    @Test
    public void generateCustomReport() {
        logger.info("Starting custom report generation...");
        
        // Add some test data
        CustomReportUtility.addMetric("Environment", "local");
        CustomReportUtility.addMetric("Base URL", "https://petstore.swagger.io/v2");
        CustomReportUtility.addMetric("Test Suite", "Custom Report Generation Test");
        
        // Create a sample test result
        CustomReportGenerator.TestResult testResult = new CustomReportGenerator.TestResult(
            "Sample Test", 
            "GenerateCustomReportTest"
        );
        testResult.setStatus("PASSED");
        testResult.setDescription("This is a sample test for custom report generation");
        testResult.addTestData("Test Data 1", "Sample Value 1");
        testResult.addTestData("Test Data 2", "Sample Value 2");
        testResult.addApiCall("GET /pet/123 - Status: 200");
        testResult.setDuration(1500L);
        
        // Add the test result to the report generator
        CustomReportGenerator reportGenerator = new CustomReportGenerator();
        reportGenerator.addTestResult(testResult);
        reportGenerator.addMetric("Total Tests", 1);
        reportGenerator.addMetric("Passed Tests", 1);
        reportGenerator.addMetric("Failed Tests", 0);
        
        // Generate the report
        String reportPath = reportGenerator.generateReport();
        
        if (reportPath != null) {
            logger.info("✅ Custom report generated successfully: {}", reportPath);
        } else {
            logger.error("❌ Failed to generate custom report");
        }
    }
} 