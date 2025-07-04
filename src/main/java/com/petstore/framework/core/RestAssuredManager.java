package com.petstore.framework.core;

import com.petstore.framework.config.TestConfig;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * RestAssured Manager for centralized HTTP client configuration
 * Handles authentication, headers, timeouts, and common request/response operations
 */
public class RestAssuredManager {
    private static final Logger logger = LogManager.getLogger(RestAssuredManager.class);
    private static RestAssuredManager instance;
    private final TestConfig config;
    private RequestSpecification defaultRequestSpec;
    private ResponseSpecification defaultResponseSpec;
    
    private RestAssuredManager() {
        this.config = TestConfig.getInstance();
        initializeRestAssured();
    }
    
    public static synchronized RestAssuredManager getInstance() {
        if (instance == null) {
            instance = new RestAssuredManager();
        }
        return instance;
    }
    
    private void initializeRestAssured() {
        // Configure RestAssured defaults
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL);
        RestAssured.useRelaxedHTTPSValidation();
        
        // Set base URI
        RestAssured.baseURI = config.getBaseUrl();
        
        // Create default request specification
        defaultRequestSpec = new RequestSpecBuilder()
                .setBaseUri(config.getBaseUrl())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addHeader("User-Agent", "PetStore-API-Test-Framework/1.0")
                .addHeader("X-Request-ID", generateRequestId())
                .log(LogDetail.ALL)
                .build();
        
        // Create default response specification
        defaultResponseSpec = new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .expectResponseTime(org.hamcrest.Matchers.lessThan(config.getTimeout() * 1000L))
                .log(LogDetail.ALL)
                .build();
        
        logger.info("RestAssured initialized with base URL: {}", config.getBaseUrl());
    }
    
    private String generateRequestId() {
        return "req-" + System.currentTimeMillis() + "-" + Thread.currentThread().getId();
    }
    
    /**
     * Get default request specification
     */
    public RequestSpecification getDefaultRequestSpec() {
        return defaultRequestSpec;
    }
    
    /**
     * Get default response specification
     */
    public ResponseSpecification getDefaultResponseSpec() {
        return defaultResponseSpec;
    }
    
    /**
     * Create authenticated request specification
     */
    public RequestSpecification getAuthenticatedRequestSpec() {
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .addRequestSpecification(defaultRequestSpec);
        
        // Add API key if available
        String apiKey = config.getApiKey();
        if (apiKey != null && !apiKey.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + apiKey);
        }
        
        return builder.build();
    }
    
    /**
     * Create request specification with custom headers
     */
    public RequestSpecification getRequestSpecWithHeaders(Map<String, String> headers) {
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .addRequestSpecification(defaultRequestSpec);
        
        headers.forEach(builder::addHeader);
        
        return builder.build();
    }
    
    /**
     * Create request specification for form data
     */
    public RequestSpecification getFormDataRequestSpec() {
        return new RequestSpecBuilder()
                .addRequestSpecification(defaultRequestSpec)
                .setContentType(ContentType.URLENC)
                .build();
    }
    
    /**
     * Create request specification for XML content
     */
    public RequestSpecification getXmlRequestSpec() {
        return new RequestSpecBuilder()
                .addRequestSpecification(defaultRequestSpec)
                .setContentType(ContentType.XML)
                .setAccept(ContentType.XML)
                .build();
    }
    
    /**
     * Create request specification for file upload
     */
    public RequestSpecification getFileUploadRequestSpec() {
        return new RequestSpecBuilder()
                .addRequestSpecification(defaultRequestSpec)
                .setContentType(ContentType.MULTIPART)
                .build();
    }
    
    /**
     * Reset RestAssured configuration
     */
    public void resetConfiguration() {
        RestAssured.reset();
        initializeRestAssured();
        logger.info("RestAssured configuration reset");
    }
    
    /**
     * Set custom base URI
     */
    public void setBaseUri(String baseUri) {
        RestAssured.baseURI = baseUri;
        logger.info("Base URI updated to: {}", baseUri);
    }
    
    /**
     * Enable/disable SSL certificate validation
     */
    public void setSSLValidation(boolean enable) {
        if (enable) {
            RestAssured.useRelaxedHTTPSValidation();
        } else {
            RestAssured.useRelaxedHTTPSValidation();
        }
        logger.info("SSL validation set to: {}", enable);
    }
    
    /**
     * Set proxy configuration
     */
    public void setProxy(String host, int port) {
        RestAssured.proxy(host, port);
        logger.info("Proxy configured: {}:{}", host, port);
    }
    
    /**
     * Set proxy with authentication
     */
    public void setProxyWithAuth(String host, int port, String username, String password) {
        RestAssured.proxy(host, port);
        logger.info("Proxy with authentication configured: {}:{}", host, port);
    }
    
    /**
     * Clear proxy configuration
     */
    public void clearProxy() {
        RestAssured.proxy((String) null);
        logger.info("Proxy configuration cleared");
    }
    
    /**
     * Get current configuration info
     */
    public String getConfigurationInfo() {
        return String.format("Base URI: %s, Timeout: %d seconds, Environment: %s",
                RestAssured.baseURI, config.getTimeout(), config.getEnvironment());
    }
} 