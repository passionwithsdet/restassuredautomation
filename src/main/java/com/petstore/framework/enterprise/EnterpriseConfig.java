package com.petstore.framework.enterprise;

import com.petstore.framework.config.TestConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enterprise Configuration Management for PetStore API Test Framework
 * Supports multiple environments, cloud providers, and enterprise features
 */
public class EnterpriseConfig {
    private static final Logger logger = LogManager.getLogger(EnterpriseConfig.class);
    private static EnterpriseConfig instance;
    private final Properties properties;
    private final ConcurrentHashMap<String, Object> cache;
    
    // Environment types
    public enum Environment {
        LOCAL, DEV, QA, STAGING, PROD, AWS, AZURE, GCP
    }
    

    

    
    private EnterpriseConfig() {
        this.properties = new Properties();
        this.cache = new ConcurrentHashMap<>();
        loadConfiguration();
    }
    
    public static synchronized EnterpriseConfig getInstance() {
        if (instance == null) {
            instance = new EnterpriseConfig();
        }
        return instance;
    }
    
    /**
     * Load configuration from multiple sources
     */
    private void loadConfiguration() {
        try {
            // Load from system properties first
            loadFromSystemProperties();
            
            // Load from environment variables
            loadFromEnvironmentVariables();
            
            // Load from config files
            loadFromConfigFiles();
            
            logger.info("Enterprise configuration loaded successfully");
            
        } catch (Exception e) {
            logger.error("Failed to load enterprise configuration", e);
            throw new RuntimeException("Configuration loading failed", e);
        }
    }
    
    /**
     * Load configuration from system properties
     */
    private void loadFromSystemProperties() {
        Properties sysProps = System.getProperties();
        for (String key : sysProps.stringPropertyNames()) {
            if (key.startsWith("petstore.") || key.startsWith("enterprise.")) {
                properties.setProperty(key, sysProps.getProperty(key));
            }
        }
    }
    
    /**
     * Load configuration from environment variables
     */
    private void loadFromEnvironmentVariables() {
        String[] envVars = {
            "TEST_ENVIRONMENT", "API_KEY", "MONGODB_PASSWORD",
            "SLACK_WEBHOOK"
        };
        
        for (String envVar : envVars) {
            String value = System.getenv(envVar);
            if (value != null && !value.isEmpty()) {
                properties.setProperty("enterprise." + envVar.toLowerCase(), value);
            }
        }
    }
    
    /**
     * Load configuration from config files
     */
    private void loadFromConfigFiles() {
        String[] configFiles = {
            "config.properties",
            "enterprise-config.properties",
            "security-config.properties"
        };
        
        for (String configFile : configFiles) {
            try (InputStream input = new FileInputStream(configFile)) {
                Properties fileProps = new Properties();
                fileProps.load(input);
                properties.putAll(fileProps);
                logger.debug("Loaded configuration from: {}", configFile);
            } catch (IOException e) {
                logger.debug("Config file not found: {}", configFile);
            }
        }
    }
    
    /**
     * Get property value with caching
     */
    public String getProperty(String key) {
        return getProperty(key, null);
    }
    
    /**
     * Get property value with default and caching
     */
    public String getProperty(String key, String defaultValue) {
        return cache.computeIfAbsent(key, k -> properties.getProperty(k, defaultValue)).toString();
    }
    
    /**
     * Get integer property
     */
    public int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                logger.warn("Invalid integer value for property {}: {}", key, value);
            }
        }
        return defaultValue;
    }
    
    /**
     * Get boolean property
     */
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        if (value != null) {
            return Boolean.parseBoolean(value);
        }
        return defaultValue;
    }
    
    /**
     * Get current environment
     */
    public Environment getEnvironment() {
        String env = getProperty("enterprise.test.environment", "local");
        try {
            return Environment.valueOf(env.toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid environment: {}, using LOCAL", env);
            return Environment.LOCAL;
        }
    }
    

    

    
    /**
     * Check if feature is enabled
     */
    public boolean isFeatureEnabled(String feature) {
        return getBooleanProperty("enterprise.feature." + feature, false);
    }
    
    /**
     * Get API configuration
     */
    public ApiConfig getApiConfig() {
        return new ApiConfig(
            getProperty("enterprise.api.base.url"),
            getIntProperty("enterprise.api.timeout", 30000),
            getIntProperty("enterprise.api.retry.count", 3),
            getProperty("enterprise.api.key")
        );
    }
    
    /**
     * Get database configuration
     */
    public DatabaseConfig getDatabaseConfig() {
        return new DatabaseConfig(
            getProperty("enterprise.mongodb.uri"),
            getProperty("enterprise.mongodb.database"),
            getProperty("enterprise.mongodb.password")
        );
    }
    
    /**
     * Get reporting configuration
     */
    public ReportingConfig getReportingConfig() {
        return new ReportingConfig(
            getProperty("enterprise.report.path", "/app/reports"),
            getProperty("enterprise.allure.results.path", "/app/allure-results"),
            getBooleanProperty("enterprise.report.allure", true),
            getBooleanProperty("enterprise.report.extent", true),
            getBooleanProperty("enterprise.report.custom", true)
        );
    }
    

    

    
    /**
     * Clear cache
     */
    public void clearCache() {
        cache.clear();
        logger.debug("Configuration cache cleared");
    }
    
    /**
     * Reload configuration
     */
    public void reload() {
        clearCache();
        loadConfiguration();
        logger.info("Configuration reloaded");
    }
    
    // Configuration classes
    public static class ApiConfig {
        private final String baseUrl;
        private final int timeout;
        private final int retryCount;
        private final String apiKey;
        
        public ApiConfig(String baseUrl, int timeout, int retryCount, String apiKey) {
            this.baseUrl = baseUrl;
            this.timeout = timeout;
            this.retryCount = retryCount;
            this.apiKey = apiKey;
        }
        
        // Getters
        public String getBaseUrl() { return baseUrl; }
        public int getTimeout() { return timeout; }
        public int getRetryCount() { return retryCount; }
        public String getApiKey() { return apiKey; }
    }
    
    public static class DatabaseConfig {
        private final String uri;
        private final String database;
        private final String password;
        
        public DatabaseConfig(String uri, String database, String password) {
            this.uri = uri;
            this.database = database;
            this.password = password;
        }
        
        // Getters
        public String getUri() { return uri; }
        public String getDatabase() { return database; }
        public String getPassword() { return password; }
    }
    
    public static class ReportingConfig {
        private final String reportPath;
        private final String allureResultsPath;
        private final boolean allureEnabled;
        private final boolean extentEnabled;
        private final boolean customEnabled;
        
        public ReportingConfig(String reportPath, String allureResultsPath, 
                             boolean allureEnabled, boolean extentEnabled, boolean customEnabled) {
            this.reportPath = reportPath;
            this.allureResultsPath = allureResultsPath;
            this.allureEnabled = allureEnabled;
            this.extentEnabled = extentEnabled;
            this.customEnabled = customEnabled;
        }
        
        // Getters
        public String getReportPath() { return reportPath; }
        public String getAllureResultsPath() { return allureResultsPath; }
        public boolean isAllureEnabled() { return allureEnabled; }
        public boolean isExtentEnabled() { return extentEnabled; }
        public boolean isCustomEnabled() { return customEnabled; }
    }
    

    

} 