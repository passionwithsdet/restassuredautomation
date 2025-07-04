package com.petstore.framework.reporting;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Custom TestNG Listener for PetStore API Test Framework
 * Automatically collects test results and generates custom reports
 */
public class CustomReportListener implements ITestListener {
    private static final Logger logger = LogManager.getLogger(CustomReportListener.class);
    private final CustomReportGenerator reportGenerator;
    private final ConcurrentHashMap<String, CustomReportGenerator.TestResult> testResults;
    
    public CustomReportListener() {
        this.reportGenerator = new CustomReportGenerator();
        this.testResults = new ConcurrentHashMap<>();
    }
    
    @Override
    public void onTestStart(ITestResult result) {
        String testId = getTestId(result);
        CustomReportGenerator.TestResult testResult = new CustomReportGenerator.TestResult(
            result.getName(),
            result.getTestClass().getName()
        );
        
        // Add test description if available
        if (result.getMethod().getDescription() != null && !result.getMethod().getDescription().isEmpty()) {
            testResult.setDescription(result.getMethod().getDescription());
        }
        
        // Add test parameters as test data
        Object[] parameters = result.getParameters();
        if (parameters != null) {
            for (int i = 0; i < parameters.length; i++) {
                testResult.addTestData("Parameter_" + i, String.valueOf(parameters[i]));
            }
        }
        
        testResults.put(testId, testResult);
        logger.debug("Test started: {} - {}", testId, result.getName());
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        String testId = getTestId(result);
        CustomReportGenerator.TestResult testResult = testResults.get(testId);
        
        if (testResult != null) {
            testResult.setStatus("PASSED");
            testResult.setEndTime(java.time.LocalDateTime.now());
            testResult.setDuration(result.getEndMillis() - result.getStartMillis());
            
            // Add success message
            testResult.addTestData("Result", "Test passed successfully");
            
            logger.debug("Test passed: {} - Duration: {}ms", testId, testResult.getDuration());
        }
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        String testId = getTestId(result);
        CustomReportGenerator.TestResult testResult = testResults.get(testId);
        
        if (testResult != null) {
            testResult.setStatus("FAILED");
            testResult.setEndTime(java.time.LocalDateTime.now());
            testResult.setDuration(result.getEndMillis() - result.getStartMillis());
            
            // Add error details
            Throwable throwable = result.getThrowable();
            if (throwable != null) {
                testResult.setErrorMessage(throwable.getMessage());
                testResult.setStackTrace(getStackTrace(throwable));
            }
            
            testResult.addTestData("Result", "Test failed");
            
            logger.debug("Test failed: {} - Error: {}", testId, testResult.getErrorMessage());
        }
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        String testId = getTestId(result);
        CustomReportGenerator.TestResult testResult = testResults.get(testId);
        
        if (testResult != null) {
            testResult.setStatus("SKIPPED");
            testResult.setEndTime(java.time.LocalDateTime.now());
            testResult.setDuration(result.getEndMillis() - result.getStartMillis());
            
            // Add skip reason
            Throwable throwable = result.getThrowable();
            if (throwable != null) {
                testResult.setErrorMessage(throwable.getMessage());
            }
            
            testResult.addTestData("Result", "Test skipped");
            
            logger.debug("Test skipped: {} - Reason: {}", testId, testResult.getErrorMessage());
        }
    }
    
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        onTestFailure(result);
    }
    
    @Override
    public void onStart(ITestContext context) {
        logger.info("Test suite started: {}", context.getName());
        
        // Add suite information to metrics
        reportGenerator.addMetric("suiteName", context.getName());
        reportGenerator.addMetric("suiteStartTime", java.time.LocalDateTime.now());
    }
    
    @Override
    public void onFinish(ITestContext context) {
        logger.info("Test suite finished: {}", context.getName());
        
        // Add all test results to the report generator
        for (CustomReportGenerator.TestResult testResult : testResults.values()) {
            reportGenerator.addTestResult(testResult);
        }
        
        // Add suite completion metrics
        reportGenerator.addMetric("suiteEndTime", java.time.LocalDateTime.now());
        reportGenerator.addMetric("suiteDuration", context.getEndDate().getTime() - context.getStartDate().getTime());
        
        // Generate the report
        String reportPath = reportGenerator.generateReport();
        if (reportPath != null) {
            logger.info("Custom report generated successfully: {}", reportPath);
        } else {
            logger.error("Failed to generate custom report");
        }
    }
    
    /**
     * Generate unique test ID
     */
    private String getTestId(ITestResult result) {
        return result.getTestClass().getName() + "." + result.getName() + "_" + result.getStartMillis();
    }
    
    /**
     * Get formatted stack trace
     */
    private String getStackTrace(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        sb.append(throwable.toString()).append("\n");
        
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        for (StackTraceElement element : stackTrace) {
            sb.append("\tat ").append(element.toString()).append("\n");
        }
        
        return sb.toString();
    }
    
    /**
     * Add API call to current test result
     */
    public void addApiCall(String apiCall) {
        // This method can be called from test methods to log API calls
        // Implementation would need to track the current test context
        logger.debug("API Call: {}", apiCall);
    }
    
    /**
     * Get the report generator instance
     */
    public CustomReportGenerator getReportGenerator() {
        return reportGenerator;
    }
} 