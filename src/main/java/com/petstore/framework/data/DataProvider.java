package com.petstore.framework.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petstore.framework.config.TestConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * Comprehensive Data Provider for data-driven testing
 * Supports Excel, JSON, and Database data sources
 */
public class DataProvider {
    private static final Logger logger = LogManager.getLogger(DataProvider.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final TestConfig config;
    
    public DataProvider() {
        this.config = TestConfig.getInstance();
    }
    
    /**
     * Excel Data Provider
     */
    @org.testng.annotations.DataProvider(name = "excelData")
    public static Object[][] getExcelData(String filePath, String sheetName) {
        List<Map<String, String>> data = new ArrayList<>();
        
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                logger.error("Sheet '{}' not found in file: {}", sheetName, filePath);
                return new Object[0][0];
            }
            
            // Get headers from first row
            Row headerRow = sheet.getRow(0);
            List<String> headers = new ArrayList<>();
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                headers.add(cell != null ? cell.toString() : "");
            }
            
            // Get data from remaining rows
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    Map<String, String> rowData = new HashMap<>();
                    for (int j = 0; j < headers.size(); j++) {
                        Cell cell = row.getCell(j);
                        String value = "";
                        if (cell != null) {
                            switch (cell.getCellType()) {
                                case STRING:
                                    value = cell.getStringCellValue();
                                    break;
                                case NUMERIC:
                                    if (DateUtil.isCellDateFormatted(cell)) {
                                        value = cell.getDateCellValue().toString();
                                    } else {
                                        value = String.valueOf((long) cell.getNumericCellValue());
                                    }
                                    break;
                                case BOOLEAN:
                                    value = String.valueOf(cell.getBooleanCellValue());
                                    break;
                                default:
                                    value = "";
                            }
                        }
                        rowData.put(headers.get(j), value);
                    }
                    data.add(rowData);
                }
            }
            
        } catch (IOException e) {
            logger.error("Error reading Excel file: {}", filePath, e);
        }
        
        return convertToObjectArray(data);
    }
    
    /**
     * JSON Data Provider
     */
    @org.testng.annotations.DataProvider(name = "jsonData")
    public static Object[][] getJsonData(String filePath) {
        try {
            List<Map<String, Object>> data = objectMapper.readValue(
                new File(filePath), 
                new TypeReference<List<Map<String, Object>>>() {}
            );
            return convertToObjectArray(data);
        } catch (IOException e) {
            logger.error("Error reading JSON file: {}", filePath, e);
            return new Object[0][0];
        }
    }
    
    /**
     * Database Data Provider
     */
    @org.testng.annotations.DataProvider(name = "dbData")
    public Object[][] getDatabaseData(String query) {
        List<Map<String, Object>> data = new ArrayList<>();
        
        try (Connection connection = getDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = resultSet.getObject(i);
                    row.put(columnName, value);
                }
                data.add(row);
            }
            
        } catch (SQLException e) {
            logger.error("Error executing database query: {}", query, e);
        }
        
        return convertToObjectArray(data);
    }
    
    /**
     * CSV Data Provider
     */
    @org.testng.annotations.DataProvider(name = "csvData")
    public static Object[][] getCsvData(String filePath) {
        List<Map<String, String>> data = new ArrayList<>();
        
        try (Scanner scanner = new Scanner(new File(filePath))) {
            if (scanner.hasNextLine()) {
                // Read headers
                String headerLine = scanner.nextLine();
                String[] headers = headerLine.split(",");
                
                // Read data rows
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] values = line.split(",");
                    Map<String, String> row = new HashMap<>();
                    
                    for (int i = 0; i < headers.length && i < values.length; i++) {
                        row.put(headers[i].trim(), values[i].trim());
                    }
                    data.add(row);
                }
            }
        } catch (IOException e) {
            logger.error("Error reading CSV file: {}", filePath, e);
        }
        
        return convertToObjectArray(data);
    }
    
    /**
     * Dynamic Data Provider with parameter substitution
     */
    @org.testng.annotations.DataProvider(name = "dynamicData")
    public static Object[][] getDynamicData(String template, Map<String, String> parameters) {
        List<Map<String, String>> data = new ArrayList<>();
        
        // Replace placeholders in template with actual values
        String processedTemplate = template;
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            processedTemplate = processedTemplate.replace("${" + entry.getKey() + "}", entry.getValue());
        }
        
        // Parse the processed template (assuming JSON format)
        try {
            List<Map<String, Object>> templateData = objectMapper.readValue(
                processedTemplate, 
                new TypeReference<List<Map<String, Object>>>() {}
            );
            
            for (Map<String, Object> item : templateData) {
                Map<String, String> stringMap = new HashMap<>();
                for (Map.Entry<String, Object> entry : item.entrySet()) {
                    stringMap.put(entry.getKey(), String.valueOf(entry.getValue()));
                }
                data.add(stringMap);
            }
        } catch (IOException e) {
            logger.error("Error processing dynamic data template", e);
        }
        
        return convertToObjectArray(data);
    }
    
    /**
     * Random Data Generator
     */
    @org.testng.annotations.DataProvider(name = "randomData")
    public static Object[][] getRandomData(int count, String... fields) {
        List<Map<String, String>> data = new ArrayList<>();
        Random random = new Random();
        
        for (int i = 0; i < count; i++) {
            Map<String, String> row = new HashMap<>();
            for (String field : fields) {
                row.put(field, generateRandomValue(field, random));
            }
            data.add(row);
        }
        
        return convertToObjectArray(data);
    }
    
    private static String generateRandomValue(String field, Random random) {
        if (field.toLowerCase().contains("id")) {
            return String.valueOf(random.nextInt(10000));
        } else if (field.toLowerCase().contains("name")) {
            return "TestName" + random.nextInt(1000);
        } else if (field.toLowerCase().contains("email")) {
            return "test" + random.nextInt(1000) + "@example.com";
        } else if (field.toLowerCase().contains("phone")) {
            return "+1" + (random.nextInt(900000000) + 100000000);
        } else {
            return "value" + random.nextInt(1000);
        }
    }
    
    private Connection getDatabaseConnection() throws SQLException {
        // This method is kept for backward compatibility but now uses MongoDB
        // For MongoDB, we don't use JDBC connections
        throw new SQLException("Database connections are not supported. Use MongoDB data provider instead.");
    }
    
    private static Object[][] convertToObjectArray(List<?> data) {
        if (data.isEmpty()) {
            return new Object[0][0];
        }
        
        Object[][] result = new Object[data.size()][1];
        for (int i = 0; i < data.size(); i++) {
            result[i][0] = data.get(i);
        }
        return result;
    }
    
    /**
     * Utility method to get test data by test name
     */
    public static Object[][] getTestData(String testName) {
        String dataFile = "src/test/resources/testdata/" + testName + ".json";
        return getJsonData(dataFile);
    }
    
    /**
     * Utility method to get Excel data by test name
     */
    public static Object[][] getExcelTestData(String testName, String sheetName) {
        String dataFile = "src/test/resources/testdata/" + testName + ".xlsx";
        return getExcelData(dataFile, sheetName);
    }
} 