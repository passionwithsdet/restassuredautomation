package com.petstore.framework.reporting;

import com.petstore.framework.config.TestConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Custom HTML Report Generator with interactive charts and detailed test information
 */
public class CustomReportGenerator {
    private static final Logger logger = LogManager.getLogger(CustomReportGenerator.class);
    private final TestConfig config;
    private final String reportPath;
    private final List<TestResult> testResults;
    private final Map<String, Object> testMetrics;

    public CustomReportGenerator() {
        this.config = TestConfig.getInstance();
        this.reportPath = config.getReportPath();
        this.testResults = new ArrayList<>();
        this.testMetrics = new HashMap<>();
    }

    /**
     * Test result data class
     */
    public static class TestResult {
        private String testName;
        private String testClass;
        private String status;
        private long duration;
        private String description;
        private String errorMessage;
        private String stackTrace;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Map<String, String> testData;
        private List<String> apiCalls;

        public TestResult() {
            this.testData = new HashMap<>();
            this.apiCalls = new ArrayList<>();
        }

        public TestResult(String testName, String testClass) {
            this();
            this.testName = testName;
            this.testClass = testClass;
        }

        // Getters and Setters
        public String getTestName() { return testName; }
        public void setTestName(String testName) { this.testName = testName; }

        public String getTestClass() { return testClass; }
        public void setTestClass(String testClass) { this.testClass = testClass; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public long getDuration() { return duration; }
        public void setDuration(long duration) { this.duration = duration; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

        public String getStackTrace() { return stackTrace; }
        public void setStackTrace(String stackTrace) { this.stackTrace = stackTrace; }

        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

        public LocalDateTime getEndTime() { return endTime; }
        public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

        public Map<String, String> getTestData() { return testData; }
        public void setTestData(Map<String, String> testData) { this.testData = testData; }

        public List<String> getApiCalls() { return apiCalls; }
        public void setApiCalls(List<String> apiCalls) { this.apiCalls = apiCalls; }

        public void addApiCall(String apiCall) {
            this.apiCalls.add(apiCall);
        }

        public void addTestData(String key, String value) {
            this.testData.put(key, value);
        }
    }

    /**
     * Add test result to the report
     */
    public void addTestResult(TestResult result) {
        testResults.add(result);
        logger.debug("Added test result: {} - {}", result.getTestName(), result.getStatus());
    }

    /**
     * Add test metric
     */
    public void addMetric(String key, Object value) {
        testMetrics.put(key, value);
    }

    /**
     * Generate comprehensive HTML report
     */
    public String generateReport() {
        try {
            // Create reports directory if it doesn't exist
            File reportDir = new File(reportPath);
            if (!reportDir.exists()) {
                reportDir.mkdirs();
            }

            // Generate timestamp for report name
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String reportFileName = "petstore_api_report_" + timestamp + ".html";
            String reportFilePath = reportPath + File.separator + reportFileName;

            // Calculate metrics
            calculateMetrics();

            // Generate HTML content
            String htmlContent = generateHtmlContent();

            // Write to file
            Files.write(Paths.get(reportFilePath), htmlContent.getBytes());

            logger.info("Custom report generated: {}", reportFilePath);
            return reportFilePath;

        } catch (IOException e) {
            logger.error("Error generating custom report", e);
            return null;
        }
    }

    /**
     * Calculate test metrics
     */
    private void calculateMetrics() {
        int totalTests = testResults.size();
        int passedTests = 0;
        int failedTests = 0;
        int skippedTests = 0;
        long totalDuration = 0;

        for (TestResult result : testResults) {
            totalDuration += result.getDuration();
            switch (result.getStatus().toLowerCase()) {
                case "pass":
                case "passed":
                    passedTests++;
                    break;
                case "fail":
                case "failed":
                    failedTests++;
                    break;
                case "skip":
                case "skipped":
                    skippedTests++;
                    break;
            }
        }

        double passRate = totalTests > 0 ? (double) passedTests / totalTests * 100 : 0;
        double averageDuration = totalTests > 0 ? (double) totalDuration / totalTests : 0;

        testMetrics.put("totalTests", totalTests);
        testMetrics.put("passedTests", passedTests);
        testMetrics.put("failedTests", failedTests);
        testMetrics.put("skippedTests", skippedTests);
        testMetrics.put("passRate", String.format("%.2f", passRate));
        testMetrics.put("totalDuration", totalDuration);
        testMetrics.put("averageDuration", String.format("%.2f", averageDuration));
        testMetrics.put("executionTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    /**
     * Generate HTML content with charts and detailed information
     */
    private String generateHtmlContent() {
        StringBuilder html = new StringBuilder();

        // HTML Header
        html.append("<!DOCTYPE html>\n");
        html.append("<html lang=\"en\">\n");
        html.append("<head>\n");
        html.append("    <meta charset=\"UTF-8\">\n");
        html.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        html.append("    <title>PetStore API Test Report</title>\n");
        html.append("    <script src=\"https://cdn.jsdelivr.net/npm/chart.js\"></script>\n");
        html.append("    <style>\n");
        html.append(generateCssStyles());
        html.append("    </style>\n");
        html.append("</head>\n");
        html.append("<body>\n");

        // Header
        html.append(generateHeader());

        // Summary Section
        html.append(generateSummarySection());

        // Charts Section
        html.append(generateChartsSection());

        // Detailed Results Section
        html.append(generateDetailedResultsSection());

        // Footer
        html.append(generateFooter());

        html.append("</body>\n");
        html.append("</html>");

        return html.toString();
    }

    /**
     * Generate CSS styles
     */
    private String generateCssStyles() {
        StringBuilder css = new StringBuilder();

        css.append("* {\n");
        css.append("    margin: 0;\n");
        css.append("    padding: 0;\n");
        css.append("    box-sizing: border-box;\n");
        css.append("}\n");

        css.append("body {\n");
        css.append("    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;\n");
        css.append("    line-height: 1.6;\n");
        css.append("    color: #333;\n");
        css.append("    background-color: #f5f5f5;\n");
        css.append("}\n");

        css.append(".container {\n");
        css.append("    max-width: 1200px;\n");
        css.append("    margin: 0 auto;\n");
        css.append("    padding: 20px;\n");
        css.append("}\n");

        css.append(".header {\n");
        css.append("    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);\n");
        css.append("    color: white;\n");
        css.append("    padding: 30px 0;\n");
        css.append("    text-align: center;\n");
        css.append("    border-radius: 10px;\n");
        css.append("    margin-bottom: 30px;\n");
        css.append("    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);\n");
        css.append("}\n");

        css.append(".header h1 {\n");
        css.append("    font-size: 2.5em;\n");
        css.append("    margin-bottom: 10px;\n");
        css.append("}\n");

        css.append(".header p {\n");
        css.append("    font-size: 1.1em;\n");
        css.append("    opacity: 0.9;\n");
        css.append("}\n");

        css.append(".summary-grid {\n");
        css.append("    display: grid;\n");
        css.append("    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));\n");
        css.append("    gap: 20px;\n");
        css.append("    margin-bottom: 30px;\n");
        css.append("}\n");

        css.append(".summary-card {\n");
        css.append("    background: white;\n");
        css.append("    padding: 25px;\n");
        css.append("    border-radius: 10px;\n");
        css.append("    text-align: center;\n");
        css.append("    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);\n");
        css.append("    transition: transform 0.3s ease;\n");
        css.append("}\n");

        css.append(".summary-card:hover {\n");
        css.append("    transform: translateY(-5px);\n");
        css.append("}\n");

        css.append(".summary-card h3 {\n");
        css.append("    font-size: 2em;\n");
        css.append("    margin-bottom: 10px;\n");
        css.append("    color: #667eea;\n");
        css.append("}\n");

        css.append(".summary-card.passed h3 {\n");
        css.append("    color: #28a745;\n");
        css.append("}\n");

        css.append(".summary-card.failed h3 {\n");
        css.append("    color: #dc3545;\n");
        css.append("}\n");

        css.append(".summary-card.skipped h3 {\n");
        css.append("    color: #ffc107;\n");
        css.append("}\n");

        css.append(".charts-section {\n");
        css.append("    background: white;\n");
        css.append("    padding: 30px;\n");
        css.append("    border-radius: 10px;\n");
        css.append("    margin-bottom: 30px;\n");
        css.append("    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);\n");
        css.append("}\n");

        css.append(".charts-grid {\n");
        css.append("    display: grid;\n");
        css.append("    grid-template-columns: 1fr 1fr;\n");
        css.append("    gap: 30px;\n");
        css.append("    margin-top: 20px;\n");
        css.append("}\n");

        css.append(".chart-container {\n");
        css.append("    background: #f8f9fa;\n");
        css.append("    padding: 20px;\n");
        css.append("    border-radius: 8px;\n");
        css.append("    height: 300px;\n");
        css.append("}\n");

        css.append(".results-section {\n");
        css.append("    background: white;\n");
        css.append("    padding: 30px;\n");
        css.append("    border-radius: 10px;\n");
        css.append("    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);\n");
        css.append("}\n");

        css.append(".test-result {\n");
        css.append("    border: 1px solid #dee2e6;\n");
        css.append("    border-radius: 8px;\n");
        css.append("    margin-bottom: 15px;\n");
        css.append("    overflow: hidden;\n");
        css.append("}\n");

        css.append(".test-header {\n");
        css.append("    display: flex;\n");
        css.append("    justify-content: space-between;\n");
        css.append("    align-items: center;\n");
        css.append("    padding: 15px 20px;\n");
        css.append("    background: #f8f9fa;\n");
        css.append("    cursor: pointer;\n");
        css.append("    transition: background-color 0.3s ease;\n");
        css.append("}\n");

        css.append(".test-header:hover {\n");
        css.append("    background: #e9ecef;\n");
        css.append("}\n");

        css.append(".test-header.pass {\n");
        css.append("    border-left: 5px solid #28a745;\n");
        css.append("}\n");

        css.append(".test-header.fail {\n");
        css.append("    border-left: 5px solid #dc3545;\n");
        css.append("}\n");

        css.append(".test-header.skip {\n");
        css.append("    border-left: 5px solid #ffc107;\n");
        css.append("}\n");

        css.append(".test-name {\n");
        css.append("    font-weight: 600;\n");
        css.append("    color: #333;\n");
        css.append("}\n");

        css.append(".test-status {\n");
        css.append("    padding: 5px 12px;\n");
        css.append("    border-radius: 20px;\n");
        css.append("    font-size: 0.9em;\n");
        css.append("    font-weight: 600;\n");
        css.append("}\n");

        css.append(".status-pass {\n");
        css.append("    background: #d4edda;\n");
        css.append("    color: #155724;\n");
        css.append("}\n");

        css.append(".status-fail {\n");
        css.append("    background: #f8d7da;\n");
        css.append("    color: #721c24;\n");
        css.append("}\n");

        css.append(".status-skip {\n");
        css.append("    background: #fff3cd;\n");
        css.append("    color: #856404;\n");
        css.append("}\n");

        css.append(".test-details {\n");
        css.append("    padding: 20px;\n");
        css.append("    display: none;\n");
        css.append("    background: white;\n");
        css.append("}\n");

        css.append(".test-details.show {\n");
        css.append("    display: block;\n");
        css.append("}\n");

        css.append(".detail-row {\n");
        css.append("    display: flex;\n");
        css.append("    margin-bottom: 10px;\n");
        css.append("}\n");

        css.append(".detail-label {\n");
        css.append("    font-weight: 600;\n");
        css.append("    width: 120px;\n");
        css.append("    color: #667eea;\n");
        css.append("}\n");

        css.append(".detail-value {\n");
        css.append("    flex: 1;\n");
        css.append("}\n");

        css.append(".error-details {\n");
        css.append("    background: #fff5f5;\n");
        css.append("    border: 1px solid #fed7d7;\n");
        css.append("    border-radius: 5px;\n");
        css.append("    padding: 15px;\n");
        css.append("    margin-top: 15px;\n");
        css.append("}\n");

        css.append(".error-details pre {\n");
        css.append("    margin: 0;\n");
        css.append("    color: #c53030;\n");
        css.append("    font-size: 0.9em;\n");
        css.append("    white-space: pre-wrap;\n");
        css.append("}\n");

        css.append(".footer {\n");
        css.append("    text-align: center;\n");
        css.append("    padding: 20px;\n");
        css.append("    color: #666;\n");
        css.append("    margin-top: 30px;\n");
        css.append("}\n");

        css.append("@media (max-width: 768px) {\n");
        css.append("    .charts-grid {\n");
        css.append("        grid-template-columns: 1fr;\n");
        css.append("    }\n");
        css.append("    .summary-grid {\n");
        css.append("        grid-template-columns: repeat(2, 1fr);\n");
        css.append("    }\n");
        css.append("}\n");

        return css.toString();
    }

    /**
     * Generate header section
     */
    private String generateHeader() {
        StringBuilder header = new StringBuilder();

        header.append("<div class=\"header\">\n");
        header.append("    <h1>🐾 PetStore API Test Report</h1>\n");
        header.append("    <p>Comprehensive API Testing Results & Analytics</p>\n");
        header.append("    <p>Generated on: " + testMetrics.get("executionTime") + "</p>\n");
        header.append("</div>\n");

        return header.toString();
    }

    /**
     * Generate summary section
     */
    private String generateSummarySection() {
        StringBuilder summary = new StringBuilder();

        summary.append("<div class=\"container\">\n");
        summary.append("    <div class=\"summary-grid\">\n");
        summary.append("        <div class=\"summary-card total\">\n");
        summary.append("            <h3>" + testMetrics.get("totalTests") + "</h3>\n");
        summary.append("            <p>Total Tests</p>\n");
        summary.append("        </div>\n");
        summary.append("        <div class=\"summary-card passed\">\n");
        summary.append("            <h3>" + testMetrics.get("passedTests") + "</h3>\n");
        summary.append("            <p>Passed</p>\n");
        summary.append("        </div>\n");
        summary.append("        <div class=\"summary-card failed\">\n");
        summary.append("            <h3>" + testMetrics.get("failedTests") + "</h3>\n");
        summary.append("            <p>Failed</p>\n");
        summary.append("        </div>\n");
        summary.append("        <div class=\"summary-card skipped\">\n");
        summary.append("            <h3>" + testMetrics.get("skippedTests") + "</h3>\n");
        summary.append("            <p>Skipped</p>\n");
        summary.append("        </div>\n");
        summary.append("    </div>\n");
        summary.append("    <div class=\"summary-grid\">\n");
        summary.append("        <div class=\"summary-card\">\n");
        summary.append("            <h3>" + testMetrics.get("passRate") + "%</h3>\n");
        summary.append("            <p>Pass Rate</p>\n");
        summary.append("        </div>\n");
        summary.append("        <div class=\"summary-card\">\n");
        summary.append("            <h3>" + testMetrics.get("totalDuration") + "ms</h3>\n");
        summary.append("            <p>Total Duration</p>\n");
        summary.append("        </div>\n");
        summary.append("        <div class=\"summary-card\">\n");
        summary.append("            <h3>" + testMetrics.get("averageDuration") + "ms</h3>\n");
        summary.append("            <p>Average Duration</p>\n");
        summary.append("        </div>\n");
        summary.append("    </div>\n");
        summary.append("</div>\n");

        return summary.toString();
    }

    /**
     * Generate charts section
     */
    private String generateChartsSection() {
        StringBuilder charts = new StringBuilder();

        charts.append("<div class=\"container\">\n");
        charts.append("    <div class=\"charts-section\">\n");
        charts.append("        <h2 style=\"margin-bottom: 20px; color: #333;\">📊 Test Results Analytics</h2>\n");
        charts.append("        <div class=\"charts-grid\">\n");
        charts.append("            <div class=\"chart-container\">\n");
        charts.append("                <canvas id=\"pieChart\"></canvas>\n");
        charts.append("            </div>\n");
        charts.append("            <div class=\"chart-container\">\n");
        charts.append("                <canvas id=\"barChart\"></canvas>\n");
        charts.append("            </div>\n");
        charts.append("        </div>\n");
        charts.append("    </div>\n");
        charts.append("</div>\n");
        charts.append("<script>\n");
        charts.append("// Pie Chart\n");
        charts.append("const pieCtx = document.getElementById('pieChart').getContext('2d');\n");
        charts.append("new Chart(pieCtx, {\n");
        charts.append("    type: 'doughnut',\n");
        charts.append("    data: {\n");
        charts.append("        labels: ['Passed', 'Failed', 'Skipped'],\n");
        charts.append("        datasets: [{\n");
        charts.append("            data: [" + testMetrics.get("passedTests") + ", " + testMetrics.get("failedTests") + ", " + testMetrics.get("skippedTests") + "],\n");
        charts.append("            backgroundColor: ['#28a745', '#dc3545', '#ffc107'],\n");
        charts.append("            borderWidth: 2,\n");
        charts.append("            borderColor: '#fff'\n");
        charts.append("        }]\n");
        charts.append("    },\n");
        charts.append("    options: {\n");
        charts.append("        responsive: true,\n");
        charts.append("        maintainAspectRatio: false,\n");
        charts.append("        plugins: {\n");
        charts.append("            legend: {\n");
        charts.append("                position: 'bottom'\n");
        charts.append("            },\n");
        charts.append("            title: {\n");
        charts.append("                display: true,\n");
        charts.append("                text: 'Test Results Distribution'\n");
        charts.append("            }\n");
        charts.append("        }\n");
        charts.append("    }\n");
        charts.append("});\n");
        charts.append("// Bar Chart\n");
        charts.append("const barCtx = document.getElementById('barChart').getContext('2d');\n");
        charts.append("new Chart(barCtx, {\n");
        charts.append("    type: 'bar',\n");
        charts.append("    data: {\n");
        charts.append("        labels: ['Total Tests', 'Passed', 'Failed', 'Skipped'],\n");
        charts.append("        datasets: [{\n");
        charts.append("            label: 'Test Count',\n");
        charts.append("            data: [" + testMetrics.get("totalTests") + ", " + testMetrics.get("passedTests") + ", " + testMetrics.get("failedTests") + ", " + testMetrics.get("skippedTests") + "],\n");
        charts.append("            backgroundColor: ['#007bff', '#28a745', '#dc3545', '#ffc107'],\n");
        charts.append("            borderWidth: 1,\n");
        charts.append("            borderColor: '#fff'\n");
        charts.append("        }]\n");
        charts.append("    },\n");
        charts.append("    options: {\n");
        charts.append("        responsive: true,\n");
        charts.append("        maintainAspectRatio: false,\n");
        charts.append("        plugins: {\n");
        charts.append("            legend: {\n");
        charts.append("                display: false\n");
        charts.append("            },\n");
        charts.append("            title: {\n");
        charts.append("                display: true,\n");
        charts.append("                text: 'Test Results Overview'\n");
        charts.append("            }\n");
        charts.append("        },\n");
        charts.append("        scales: {\n");
        charts.append("            y: {\n");
        charts.append("                beginAtZero: true\n");
        charts.append("            }\n");
        charts.append("        }\n");
        charts.append("    }\n");
        charts.append("});\n");
        charts.append("</script>\n");

        return charts.toString();
    }

    /**
     * Generate detailed results section
     */
    private String generateDetailedResultsSection() {
        StringBuilder section = new StringBuilder();
        section.append("<div class=\"container\">\n");
        section.append("    <div class=\"results-section\">\n");
        section.append("        <h2 style=\"margin-bottom: 20px; color: #333;\">📋 Detailed Test Results</h2>\n");
        for (TestResult result : testResults) {
            section.append(generateTestResultHtml(result));
        }
        section.append("    </div>\n");
        section.append("</div>\n");
        section.append("<script>\n");
        section.append("function toggleDetails(element) {\n");
        section.append("    const details = element.nextElementSibling;\n");
        section.append("    details.classList.toggle('show');\n");
        section.append("}\n");
        section.append("</script>\n");

        return section.toString();
    }

    /**
     * Generate individual test result HTML
     */
    private String generateTestResultHtml(TestResult result) {
        StringBuilder html = new StringBuilder();

        String statusClass = result.getStatus().toLowerCase();
        String statusDisplay = result.getStatus().toUpperCase();

        html.append("<div class=\"test-result\">\n");
        html.append("    <div class=\"test-header ").append(statusClass).append("\" onclick=\"toggleDetails(this)\">\n");
        html.append("        <div class=\"test-name\">").append(result.getTestName()).append("</div>\n");
        html.append("        <div class=\"test-status status-").append(statusClass).append("\">").append(statusDisplay).append("</div>\n");
        html.append("    </div>\n");
        html.append("    <div class=\"test-details\">\n");

        // Test details
        html.append("        <div class=\"detail-row\">\n");
        html.append("            <div class=\"detail-label\">Class:</div>\n");
        html.append("            <div class=\"detail-value\">").append(result.getTestClass()).append("</div>\n");
        html.append("        </div>\n");

        if (result.getDescription() != null) {
            html.append("        <div class=\"detail-row\">\n");
            html.append("            <div class=\"detail-label\">Description:</div>\n");
            html.append("            <div class=\"detail-value\">").append(result.getDescription()).append("</div>\n");
            html.append("        </div>\n");
        }

        html.append("        <div class=\"detail-row\">\n");
        html.append("            <div class=\"detail-label\">Duration:</div>\n");
        html.append("            <div class=\"detail-value\">").append(result.getDuration()).append(" ms</div>\n");
        html.append("        </div>\n");

        if (result.getStartTime() != null) {
            html.append("        <div class=\"detail-row\">\n");
            html.append("            <div class=\"detail-label\">Start Time:</div>\n");
            html.append("            <div class=\"detail-value\">").append(result.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("</div>\n");
            html.append("        </div>\n");
        }

        if (result.getEndTime() != null) {
            html.append("        <div class=\"detail-row\">\n");
            html.append("            <div class=\"detail-label\">End Time:</div>\n");
            html.append("            <div class=\"detail-value\">").append(result.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("</div>\n");
            html.append("        </div>\n");
        }

        // Test data
        if (!result.getTestData().isEmpty()) {
            html.append("        <div class=\"detail-row\">\n");
            html.append("            <div class=\"detail-label\">Test Data:</div>\n");
            html.append("            <div class=\"detail-value\">\n");
            for (Map.Entry<String, String> entry : result.getTestData().entrySet()) {
                html.append("                <div><strong>").append(entry.getKey()).append(":</strong> ").append(entry.getValue()).append("</div>\n");
            }
            html.append("            </div>\n");
            html.append("        </div>\n");
        }

        // API calls
        if (!result.getApiCalls().isEmpty()) {
            html.append("        <div class=\"detail-row\">\n");
            html.append("            <div class=\"detail-label\">API Calls:</div>\n");
            html.append("            <div class=\"detail-value\">\n");
            for (String apiCall : result.getApiCalls()) {
                html.append("                <div>").append(apiCall).append("</div>\n");
            }
            html.append("            </div>\n");
            html.append("        </div>\n");
        }

        // Error details
        if (result.getErrorMessage() != null || result.getStackTrace() != null) {
            html.append("        <div class=\"error-details\">\n");
            if (result.getErrorMessage() != null) {
                html.append("            <div><strong>Error:</strong> ").append(result.getErrorMessage()).append("</div>\n");
            }
            if (result.getStackTrace() != null) {
                html.append("            <pre>").append(result.getStackTrace()).append("</pre>\n");
            }
            html.append("        </div>\n");
        }

        html.append("    </div>\n");
        html.append("</div>\n");

        return html.toString();
    }

    /**
     * Generate footer
     */
    private String generateFooter() {
        StringBuilder footer = new StringBuilder();
        footer.append("<div class=\"footer\">\n");
        footer.append("    <p>Generated by PetStore API Test Framework | ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("</p>\n");
        footer.append("</div>\n");
        return footer.toString();
    }

    // Getters
    public List<TestResult> getTestResults() {
        return new ArrayList<>(testResults);
    }

    public Map<String, Object> getTestMetrics() {
        return new HashMap<>(testMetrics);
    }
} 