package com.petstore.framework.reporting;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Custom Report Utility for PetStore API Test Framework
 * Provides easy access to custom reporting functionality
 */
public class CustomReportUtility {
    private static final Logger logger = LogManager.getLogger(CustomReportUtility.class);
    private static final ReportManager reportManager = ReportManager.getInstance();
    
    /**
     * Start tracking a test with custom reporting
     */
    public static void startCustomTest(String testName, String testClass) {
        reportManager.startTest(testName, testClass);
        logger.debug("Started custom test tracking: {} - {}", testClass, testName);
    }
    
    /**
     * End tracking a test with custom reporting
     */
    public static void endCustomTest(String status) {
        reportManager.endTest(status);
        logger.debug("Ended custom test tracking with status: {}", status);
    }
    
    /**
     * Add test data to current test
     */
    public static void addTestData(String key, String value) {
        reportManager.addTestData(key, value);
    }
    
    /**
     * Add API call details to current test
     */
    public static void addApiCall(String method, String url, String requestBody, String responseBody, int statusCode) {
        reportManager.addApiCall(method, url, requestBody, responseBody, statusCode);
    }
    
    /**
     * Add error details to current test
     */
    public static void addError(String errorMessage, String stackTrace) {
        reportManager.addError(errorMessage, stackTrace);
    }
    
    /**
     * Add error details to current test with exception
     */
    public static void addError(Exception exception) {
        String errorMessage = exception.getMessage();
        String stackTrace = getStackTrace(exception);
        addError(errorMessage, stackTrace);
    }
    
    /**
     * Add metric to report
     */
    public static void addMetric(String key, Object value) {
        reportManager.addMetric(key, value);
    }
    
    /**
     * Generate custom report
     */
    public static String generateCustomReport() {
        return reportManager.generateReport();
    }
    
    /**
     * Get formatted stack trace
     */
    private static String getStackTrace(Exception exception) {
        StringBuilder sb = new StringBuilder();
        sb.append(exception.toString()).append("\n");
        
        StackTraceElement[] stackTrace = exception.getStackTrace();
        for (StackTraceElement element : stackTrace) {
            sb.append("\tat ").append(element.toString()).append("\n");
        }
        
        return sb.toString();
    }
    
    /**
     * Log test step with custom reporting
     */
    public static void logStep(String stepName, String details) {
        addTestData("Step: " + stepName, details);
        logger.info("Test step: {} - {}", stepName, details);
    }
    
    /**
     * Log API request with custom reporting
     */
    public static void logApiRequest(String method, String url, String requestBody) {
        addApiCall(method, url, requestBody, null, 0);
        logger.info("API Request: {} {} - Body: {}", method, url, requestBody);
    }
    
    /**
     * Log API response with custom reporting
     */
    public static void logApiResponse(String method, String url, String responseBody, int statusCode) {
        addApiCall(method, url, null, responseBody, statusCode);
        logger.info("API Response: {} {} - Status: {} - Body: {}", method, url, statusCode, responseBody);
    }
    
    /**
     * Log complete API call with custom reporting
     */
    public static void logApiCall(String method, String url, String requestBody, String responseBody, int statusCode) {
        addApiCall(method, url, requestBody, responseBody, statusCode);
        logger.info("API Call: {} {} - Status: {} - Request: {} - Response: {}", 
                   method, url, statusCode, requestBody, responseBody);
    }
    
    /**
     * Log test validation with custom reporting
     */
    public static void logValidation(String validationName, String expectedValue, String actualValue, boolean passed) {
        String validationResult = String.format("Validation: %s - Expected: %s - Actual: %s - Passed: %s", 
                                              validationName, expectedValue, actualValue, passed);
        addTestData("Validation: " + validationName, validationResult);
        logger.info(validationResult);
    }
    
    /**
     * Log test data with custom reporting
     */
    public static void logTestData(String dataName, Object dataValue) {
        addTestData(dataName, String.valueOf(dataValue));
        logger.debug("Test data: {} = {}", dataName, dataValue);
    }
    
    /**
     * Clear all custom test data
     */
    public static void clear() {
        reportManager.clear();
        logger.debug("Cleared all custom test data");
    }
} 