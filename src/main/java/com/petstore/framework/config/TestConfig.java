package com.petstore.framework.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Central configuration management for the PetStore API Test Framework
 * Supports multiple environments: local, docker, kubernetes, cloud
 */
public class TestConfig {
    private static final Logger logger = LogManager.getLogger(TestConfig.class);
    private static TestConfig instance;
    private Properties properties;
    
    // Default configuration values
    private static final String DEFAULT_CONFIG_FILE = "config.properties";
    private static final String DEFAULT_BASE_URL = "https://petstore.swagger.io/v2";
    private static final String DEFAULT_ENVIRONMENT = "local";
    private static final int DEFAULT_TIMEOUT = 30;
    
    private TestConfig() {
        loadConfiguration();
    }
    
    public static synchronized TestConfig getInstance() {
        if (instance == null) {
            instance = new TestConfig();
        }
        return instance;
    }
    
    private void loadConfiguration() {
        properties = new Properties();
        String environment = System.getProperty("test.environment", DEFAULT_ENVIRONMENT);
        String configFile = System.getProperty("config.file", DEFAULT_CONFIG_FILE);
        
        try {
            // Load default properties
            loadPropertiesFromClasspath(DEFAULT_CONFIG_FILE);
            
            // Load environment-specific properties
            String envConfigFile = "config-" + environment + ".properties";
            loadPropertiesFromClasspath(envConfigFile);
            
            // Override with system properties
            overrideWithSystemProperties();
            
            logger.info("Configuration loaded for environment: {}", environment);
            logger.info("Base URL: {}", getBaseUrl());
            
        } catch (IOException e) {
            logger.warn("Could not load configuration file, using defaults: {}", e.getMessage());
            setDefaultProperties();
        }
    }
    
    private void loadPropertiesFromClasspath(String fileName) throws IOException {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (input != null) {
                properties.load(input);
                logger.debug("Loaded properties from: {}", fileName);
            }
        }
    }
    
    private void overrideWithSystemProperties() {
        Properties systemProps = System.getProperties();
        for (String key : systemProps.stringPropertyNames()) {
            if (key.startsWith("petstore.")) {
                String configKey = key.substring("petstore.".length());
                properties.setProperty(configKey, systemProps.getProperty(key));
                logger.debug("Overriding {} with system property: {}", configKey, systemProps.getProperty(key));
            }
        }
    }
    
    private void setDefaultProperties() {
        properties.setProperty("base.url", DEFAULT_BASE_URL);
        properties.setProperty("environment", DEFAULT_ENVIRONMENT);
        properties.setProperty("timeout", String.valueOf(DEFAULT_TIMEOUT));
        properties.setProperty("retry.count", "3");
        properties.setProperty("parallel.execution", "true");
        properties.setProperty("thread.count", "4");
    }
    
    // Configuration getters
    public String getBaseUrl() {
        return properties.getProperty("base.url", DEFAULT_BASE_URL);
    }
    
    public String getEnvironment() {
        return properties.getProperty("environment", DEFAULT_ENVIRONMENT);
    }
    
    public int getTimeout() {
        return Integer.parseInt(properties.getProperty("timeout", String.valueOf(DEFAULT_TIMEOUT)));
    }
    
    public int getRetryCount() {
        return Integer.parseInt(properties.getProperty("retry.count", "3"));
    }
    
    public boolean isParallelExecution() {
        return Boolean.parseBoolean(properties.getProperty("parallel.execution", "true"));
    }
    
    public int getThreadCount() {
        return Integer.parseInt(properties.getProperty("thread.count", "4"));
    }
    
    public String getApiKey() {
        return properties.getProperty("api.key", "");
    }
    
    public String getUsername() {
        return properties.getProperty("username", "");
    }
    
    public String getPassword() {
        return properties.getProperty("password", "");
    }
    
    public String getMongoDbUri() {
        return properties.getProperty("mongodb.uri", "mongodb://localhost:27017");
    }
    
    public String getMongoDbDatabase() {
        return properties.getProperty("mongodb.database", "petstore_test");
    }
    
    public String getMongoDbCollection(String collectionType) {
        return properties.getProperty("mongodb.collection." + collectionType, collectionType);
    }
    
    // Database configuration methods (for backward compatibility)
    public String getDatabaseUrl() {
        return getMongoDbUri();
    }
    
    public String getDatabaseUsername() {
        return properties.getProperty("database.username", "");
    }
    
    public String getDatabasePassword() {
        return properties.getProperty("database.password", "");
    }
    
    public String getReportPath() {
        return properties.getProperty("report.path", "target/reports");
    }
    
    public String getLogLevel() {
        return properties.getProperty("log.level", "INFO");
    }
    
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
        logger.debug("Set property: {} = {}", key, value);
    }
} 