package com.petstore.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petstore.framework.config.TestConfig;
import com.petstore.framework.core.RestAssuredManager;
import com.petstore.framework.reporting.ReportManager;
import com.petstore.models.Order;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Store API Service for PetStore API operations
 * Handles store inventory and order operations
 */
public class StoreApiService {
    private static final Logger logger = LogManager.getLogger(StoreApiService.class);
    private final RestAssuredManager restAssuredManager;
    private final ReportManager reportManager;
    private final ObjectMapper objectMapper;
    private final String basePath = "/store";
    
    public StoreApiService() {
        this.restAssuredManager = RestAssuredManager.getInstance();
        this.reportManager = ReportManager.getInstance();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Get store inventory
     */
    public Map<String, Integer> getInventory() {
        String testName = "GetInventory";
        long startTime = System.currentTimeMillis();
        
        Response response = given()
                .spec(restAssuredManager.getAuthenticatedRequestSpec())
                .when()
                .get(basePath + "/inventory")
                .then()
                .spec(restAssuredManager.getDefaultResponseSpec())
                .extract().response();
        
        long responseTime = System.currentTimeMillis() - startTime;
        
        // Log API call
        reportManager.logApiCall(testName, "GET", basePath + "/inventory", null, 
                response.getBody().asString(), response.getStatusCode(), responseTime);
        
        if (response.getStatusCode() == 200) {
            Map<String, Integer> inventory = response.as(Map.class);
            logger.info("Store inventory retrieved successfully");
            return inventory;
        } else {
            logger.error("Failed to get store inventory. Status: {}", response.getStatusCode());
            return null;
        }
    }
    
    /**
     * Create a new order
     */
    public Order createOrder(Order order) {
        String testName = "CreateOrder_" + order.getPetId();
        long startTime = System.currentTimeMillis();
        
        try {
            String requestBody = objectMapper.writeValueAsString(order);
            
            Response response = given()
                    .spec(restAssuredManager.getDefaultRequestSpec())
                    .body(requestBody)
                    .when()
                    .post(basePath + "/order")
                    .then()
                    .spec(restAssuredManager.getDefaultResponseSpec())
                    .extract().response();
            
            long responseTime = System.currentTimeMillis() - startTime;
            
            // Log API call
            reportManager.logApiCall(testName, "POST", basePath + "/order", requestBody, 
                    response.getBody().asString(), response.getStatusCode(), responseTime);
            
            if (response.getStatusCode() == 200) {
                Order createdOrder = response.as(Order.class);
                logger.info("Order created successfully: {}", createdOrder.getId());
                return createdOrder;
            } else {
                logger.error("Failed to create order. Status: {}", response.getStatusCode());
                return null;
            }
            
        } catch (JsonProcessingException e) {
            logger.error("Error serializing order object", e);
            return null;
        }
    }
    
    /**
     * Create order and return response
     */
    public Response createOrderResponse(Order order) {
        String testName = "CreateOrderResponse_" + order.getPetId();
        long startTime = System.currentTimeMillis();
        
        try {
            String requestBody = objectMapper.writeValueAsString(order);
            
            Response response = given()
                    .spec(restAssuredManager.getDefaultRequestSpec())
                    .body(requestBody)
                    .when()
                    .post(basePath + "/order")
                    .then()
                    .spec(restAssuredManager.getDefaultResponseSpec())
                    .extract().response();
            
            long responseTime = System.currentTimeMillis() - startTime;
            
            // Log API call
            reportManager.logApiCall(testName, "POST", basePath + "/order", requestBody, 
                    response.getBody().asString(), response.getStatusCode(), responseTime);
            
            return response;
            
        } catch (JsonProcessingException e) {
            logger.error("Error serializing order object", e);
            return null;
        }
    }
    
    /**
     * Get order by ID
     */
    public Order getOrderById(Long orderId) {
        String testName = "GetOrderById_" + orderId;
        long startTime = System.currentTimeMillis();
        
        Response response = given()
                .spec(restAssuredManager.getDefaultRequestSpec())
                .pathParam("orderId", orderId)
                .when()
                .get(basePath + "/order/{orderId}")
                .then()
                .spec(restAssuredManager.getDefaultResponseSpec())
                .extract().response();
        
        long responseTime = System.currentTimeMillis() - startTime;
        
        // Log API call
        reportManager.logApiCall(testName, "GET", basePath + "/order/" + orderId, null, 
                response.getBody().asString(), response.getStatusCode(), responseTime);
        
        if (response.getStatusCode() == 200) {
            Order order = response.as(Order.class);
            logger.info("Order retrieved successfully: {}", order.getId());
            return order;
        } else {
            logger.error("Failed to get order. Status: {}", response.getStatusCode());
            return null;
        }
    }
    
    /**
     * Get order by ID and return response
     */
    public Response getOrderByIdResponse(Long orderId) {
        String testName = "GetOrderByIdResponse_" + orderId;
        long startTime = System.currentTimeMillis();
        
        Response response = given()
                .spec(restAssuredManager.getDefaultRequestSpec())
                .pathParam("orderId", orderId)
                .when()
                .get(basePath + "/order/{orderId}")
                .then()
                .spec(restAssuredManager.getDefaultResponseSpec())
                .extract().response();
        
        long responseTime = System.currentTimeMillis() - startTime;
        
        // Log API call
        reportManager.logApiCall(testName, "GET", basePath + "/order/" + orderId, null, 
                response.getBody().asString(), response.getStatusCode(), responseTime);
        
        return response;
    }
    
    /**
     * Delete order by ID
     */
    public Response deleteOrder(Long orderId) {
        String testName = "DeleteOrder_" + orderId;
        long startTime = System.currentTimeMillis();
        
        Response response = given()
                .spec(restAssuredManager.getDefaultRequestSpec())
                .pathParam("orderId", orderId)
                .when()
                .delete(basePath + "/order/{orderId}")
                .then()
                .spec(restAssuredManager.getDefaultResponseSpec())
                .extract().response();
        
        long responseTime = System.currentTimeMillis() - startTime;
        
        // Log API call
        reportManager.logApiCall(testName, "DELETE", basePath + "/order/" + orderId, null, 
                response.getBody().asString(), response.getStatusCode(), responseTime);
        
        if (response.getStatusCode() == 200) {
            logger.info("Order deleted successfully: {}", orderId);
        } else {
            logger.error("Failed to delete order. Status: {}", response.getStatusCode());
        }
        
        return response;
    }
} 