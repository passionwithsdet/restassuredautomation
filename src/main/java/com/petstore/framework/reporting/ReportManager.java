package com.petstore.framework.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.petstore.framework.config.TestConfig;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Comprehensive Reporting Manager
 * Integrates ExtentReports and Allure for detailed HTML reports and dashboard integration
 */
public class ReportManager implements ITestListener {
    private static final Logger logger = LogManager.getLogger(ReportManager.class);
    private static ReportManager instance;
    private ExtentReports extentReports;
    private Map<String, ExtentTest> testMap;
    private final TestConfig config;
    private final String reportPath;
    private final CustomReportGenerator reportGenerator;
    private final ConcurrentHashMap<String, CustomReportGenerator.TestResult> currentTests;
    
    public ReportManager() {
        // No-op constructor for TestNG
        this.config = TestConfig.getInstance();
        this.reportPath = config.getReportPath();
        this.testMap = new HashMap<>();
        this.reportGenerator = new CustomReportGenerator();
        this.currentTests = new ConcurrentHashMap<>();
        initializeExtentReports();
    }
    
    public static synchronized ReportManager getInstance() {
        if (instance == null) {
            instance = new ReportManager();
        }
        return instance;
    }
    
    private void initializeExtentReports() {
        extentReports = new ExtentReports();
        
        // Create reports directory if it doesn't exist
        File reportDir = new File(reportPath);
        if (!reportDir.exists()) {
            reportDir.mkdirs();
        }
        
        // Configure Spark Reporter
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String reportFileName = "PetStore_API_Test_Report_" + timestamp + ".html";
        String reportFilePath = reportPath + File.separator + reportFileName;
        
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportFilePath);
        sparkReporter.config().setTheme(Theme.DARK);
        sparkReporter.config().setDocumentTitle("PetStore API Test Automation Report");
        sparkReporter.config().setReportName("PetStore API Test Framework");
        sparkReporter.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");
        
        // Add system information
        extentReports.setSystemInfo("Environment", config.getEnvironment());
        extentReports.setSystemInfo("Base URL", config.getBaseUrl());
        extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));
        extentReports.setSystemInfo("OS", System.getProperty("os.name"));
        extentReports.setSystemInfo("User", System.getProperty("user.name"));
        
        extentReports.attachReporter(sparkReporter);
        
        logger.info("ExtentReports initialized: {}", reportFilePath);
    }
    
    /**
     * Start test in ExtentReports
     */
    public ExtentTest startTest(String testName, String description) {
        ExtentTest test = extentReports.createTest(testName, description);
        testMap.put(testName, test);
        
        // Add to Allure
        Allure.getLifecycle().updateTestCase(testResult -> {
            testResult.setName(testName);
            testResult.setDescription(description);
        });
        
        logger.info("Started test: {}", testName);
        return test;
    }
    
    /**
     * Log test step
     */
    public void logStep(String testName, Status status, String stepName, String details) {
        ExtentTest test = testMap.get(testName);
        if (test != null) {
            test.log(status, stepName + ": " + details);
        }
        
        // Add to Allure
        Allure.step(stepName + ": " + details, () -> {
            // Step execution logic
        });
        
        logger.debug("Test step logged: {} - {} - {}", testName, stepName, details);
    }
    
    /**
     * Log API request/response
     */
    public void logApiCall(String testName, String method, String url, String requestBody, 
                          String responseBody, int statusCode, long responseTime) {
        ExtentTest test = testMap.get(testName);
        if (test != null) {
            test.info("API Call Details:");
            test.info("Method: " + method);
            test.info("URL: " + url);
            if (requestBody != null && !requestBody.isEmpty()) {
                test.info("Request Body: " + requestBody);
            }
            test.info("Response Status: " + statusCode);
            test.info("Response Time: " + responseTime + "ms");
            if (responseBody != null && !responseBody.isEmpty()) {
                test.info("Response Body: " + responseBody);
            }
        }
        
        // Add to Allure
        Allure.addAttachment("API Request", "application/json", requestBody != null ? requestBody : "");
        Allure.addAttachment("API Response", "application/json", responseBody != null ? responseBody : "");
        
        logger.info("API call logged: {} {} - Status: {} - Time: {}ms", method, url, statusCode, responseTime);
    }
    
    /**
     * Log screenshot
     */
    public void logScreenshot(String testName, String screenshotPath, String description) {
        ExtentTest test = testMap.get(testName);
        if (test != null && new File(screenshotPath).exists()) {
            test.addScreenCaptureFromPath(screenshotPath, description);
        }
        
        // Add to Allure
        Allure.addAttachment(description, "image/png", screenshotPath);
    }
    
    /**
     * Log test data
     */
    public void logTestData(String testName, Map<String, Object> testData) {
        ExtentTest test = testMap.get(testName);
        if (test != null) {
            test.info("Test Data:");
            testData.forEach((key, value) -> test.info(key + ": " + value));
        }
        
        // Add to Allure
        testData.forEach((key, value) -> 
            Allure.addAttachment(key, "text/plain", String.valueOf(value)));
    }
    
    /**
     * End test
     */
    public void endTest(String testName, Status status, String details) {
        ExtentTest test = testMap.get(testName);
        if (test != null) {
            test.log(status, details);
            testMap.remove(testName);
        }
        
        logger.info("Ended test: {} - Status: {}", testName, status);
    }
    
    /**
     * Flush and close reports
     */
    public void flushReports() {
        if (extentReports != null) {
            extentReports.flush();
            logger.info("ExtentReports flushed successfully");
        }
    }
    
    /**
     * Generate summary report
     */
    public void generateSummaryReport() {
        try {
            String summaryPath = reportPath + File.separator + "test_summary.json";
            TestSummary summary = new TestSummary();
            // Use default values since stats methods are not available
            summary.setTotalTests(testMap.size());
            summary.setPassedTests(0);
            summary.setFailedTests(0);
            summary.setSkippedTests(0);
            summary.setExecutionTime(System.currentTimeMillis());
            
            // Write summary to file
            Files.write(Paths.get(summaryPath), 
                summary.toJson().getBytes());
            
            logger.info("Summary report generated: {}", summaryPath);
        } catch (IOException e) {
            logger.error("Error generating summary report", e);
        }
    }
    
    // TestNG Listener Methods
    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getName();
        String description = result.getMethod().getDescription();
        startTest(testName, description);
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = result.getName();
        endTest(testName, Status.PASS, "Test passed successfully");
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getName();
        String errorMessage = result.getThrowable() != null ? 
            result.getThrowable().getMessage() : "Test failed";
        endTest(testName, Status.FAIL, "Test failed: " + errorMessage);
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = result.getName();
        endTest(testName, Status.SKIP, "Test skipped");
    }
    
    @Override
    public void onFinish(ITestContext context) {
        generateSummaryReport();
        flushReports();
    }
    
    /**
     * Test Summary class for JSON serialization
     */
    private static class TestSummary {
        private int totalTests;
        private int passedTests;
        private int failedTests;
        private int skippedTests;
        private long executionTime;
        
        // Getters and setters
        public int getTotalTests() { return totalTests; }
        public void setTotalTests(int totalTests) { this.totalTests = totalTests; }
        
        public int getPassedTests() { return passedTests; }
        public void setPassedTests(int passedTests) { this.passedTests = passedTests; }
        
        public int getFailedTests() { return failedTests; }
        public void setFailedTests(int failedTests) { this.failedTests = failedTests; }
        
        public int getSkippedTests() { return skippedTests; }
        public void setSkippedTests(int skippedTests) { this.skippedTests = skippedTests; }
        
        public long getExecutionTime() { return executionTime; }
        public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }
        
        public String toJson() {
            return String.format(
                "{\"totalTests\":%d,\"passedTests\":%d,\"failedTests\":%d,\"skippedTests\":%d,\"executionTime\":%d}",
                totalTests, passedTests, failedTests, skippedTests, executionTime
            );
        }
    }
    

    
    /**
     * End tracking a test
     */
    public void endTest(String status) {
        String threadId = getCurrentThreadId();
        CustomReportGenerator.TestResult testResult = currentTests.get(threadId);
        
        if (testResult != null) {
            testResult.setStatus(status);
            testResult.setEndTime(java.time.LocalDateTime.now());
            
            // Calculate duration if start time is available
            if (testResult.getStartTime() != null) {
                long duration = java.time.Duration.between(testResult.getStartTime(), testResult.getEndTime()).toMillis();
                testResult.setDuration(duration);
            }
            
            // Add to report generator
            reportGenerator.addTestResult(testResult);
            
            // Remove from current tests
            currentTests.remove(threadId);
            
            logger.debug("Ended tracking test: {} - Status: {}", testResult.getTestName(), status);
        }
    }
    
    /**
     * Add test data to current test
     */
    public void addTestData(String key, String value) {
        CustomReportGenerator.TestResult testResult = getCurrentTest();
        if (testResult != null) {
            testResult.addTestData(key, value);
            logger.debug("Added test data: {} = {}", key, value);
        }
    }
    
    /**
     * Add API call to current test
     */
    public void addApiCall(String method, String url, String requestBody, String responseBody, int statusCode) {
        CustomReportGenerator.TestResult testResult = getCurrentTest();
        if (testResult != null) {
            String apiCall = String.format("%s %s (Status: %d)", method, url, statusCode);
            if (requestBody != null && !requestBody.isEmpty()) {
                apiCall += " | Request: " + requestBody;
            }
            if (responseBody != null && !responseBody.isEmpty()) {
                apiCall += " | Response: " + responseBody;
            }
            
            testResult.addApiCall(apiCall);
            logger.debug("Added API call: {}", apiCall);
        }
    }
    
    /**
     * Add error details to current test
     */
    public void addError(String errorMessage, String stackTrace) {
        CustomReportGenerator.TestResult testResult = getCurrentTest();
        if (testResult != null) {
            testResult.setErrorMessage(errorMessage);
            testResult.setStackTrace(stackTrace);
            logger.debug("Added error details: {}", errorMessage);
        }
    }
    
    /**
     * Add metric to report
     */
    public void addMetric(String key, Object value) {
        reportGenerator.addMetric(key, value);
        logger.debug("Added metric: {} = {}", key, value);
    }
    
    /**
     * Generate report
     */
    public String generateReport() {
        String reportPath = reportGenerator.generateReport();
        if (reportPath != null) {
            logger.info("Custom report generated: {}", reportPath);
        } else {
            logger.error("Failed to generate custom report");
        }
        return reportPath;
    }
    
    /**
     * Get current test result
     */
    private CustomReportGenerator.TestResult getCurrentTest() {
        return currentTests.get(getCurrentThreadId());
    }
    
    /**
     * Get current thread ID
     */
    private String getCurrentThreadId() {
        return String.valueOf(Thread.currentThread().getId());
    }
    
    /**
     * Get report generator instance
     */
    public CustomReportGenerator getReportGenerator() {
        return reportGenerator;
    }
    
    /**
     * Clear all test data (useful for cleanup)
     */
    public void clear() {
        currentTests.clear();
        logger.debug("Cleared all test tracking data");
    }
} 