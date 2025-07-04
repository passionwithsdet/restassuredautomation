package com.petstore.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petstore.framework.config.TestConfig;
import com.petstore.framework.core.RestAssuredManager;
import com.petstore.framework.reporting.ReportManager;
import com.petstore.models.User;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static io.restassured.RestAssured.given;

/**
 * User API Service for PetStore API operations
 * Handles user management operations
 */
public class UserApiService {
    private static final Logger logger = LogManager.getLogger(UserApiService.class);
    private final RestAssuredManager restAssuredManager;
    private final ReportManager reportManager;
    private final ObjectMapper objectMapper;
    private final String basePath = "/user";
    
    public UserApiService() {
        this.restAssuredManager = RestAssuredManager.getInstance();
        this.reportManager = ReportManager.getInstance();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Create a new user
     */
    public Response createUser(User user) {
        String testName = "CreateUser_" + user.getUsername();
        long startTime = System.currentTimeMillis();
        
        try {
            String requestBody = objectMapper.writeValueAsString(user);
            
            Response response = given()
                    .spec(restAssuredManager.getDefaultRequestSpec())
                    .body(requestBody)
                    .when()
                    .post(basePath)
                    .then()
                    .spec(restAssuredManager.getDefaultResponseSpec())
                    .extract().response();
            
            long responseTime = System.currentTimeMillis() - startTime;
            
            // Log API call
            reportManager.logApiCall(testName, "POST", basePath, requestBody, 
                    response.getBody().asString(), response.getStatusCode(), responseTime);
            
            if (response.getStatusCode() == 200) {
                logger.info("User created successfully: {}", user.getUsername());
            } else {
                logger.error("Failed to create user. Status: {}", response.getStatusCode());
            }
            
            return response;
            
        } catch (JsonProcessingException e) {
            logger.error("Error serializing user object", e);
            return null;
        }
    }
    
    /**
     * Create multiple users with array
     */
    public Response createUsersWithArray(User[] users) {
        String testName = "CreateUsersWithArray";
        long startTime = System.currentTimeMillis();
        
        try {
            String requestBody = objectMapper.writeValueAsString(users);
            
            Response response = given()
                    .spec(restAssuredManager.getDefaultRequestSpec())
                    .body(requestBody)
                    .when()
                    .post(basePath + "/createWithArray")
                    .then()
                    .spec(restAssuredManager.getDefaultResponseSpec())
                    .extract().response();
            
            long responseTime = System.currentTimeMillis() - startTime;
            
            // Log API call
            reportManager.logApiCall(testName, "POST", basePath + "/createWithArray", requestBody, 
                    response.getBody().asString(), response.getStatusCode(), responseTime);
            
            if (response.getStatusCode() == 200) {
                logger.info("Users created successfully with array: {}", users.length);
            } else {
                logger.error("Failed to create users with array. Status: {}", response.getStatusCode());
            }
            
            return response;
            
        } catch (JsonProcessingException e) {
            logger.error("Error serializing users array", e);
            return null;
        }
    }
    
    /**
     * Create multiple users with list
     */
    public Response createUsersWithList(List<User> users) {
        String testName = "CreateUsersWithList";
        long startTime = System.currentTimeMillis();
        
        try {
            String requestBody = objectMapper.writeValueAsString(users);
            
            Response response = given()
                    .spec(restAssuredManager.getDefaultRequestSpec())
                    .body(requestBody)
                    .when()
                    .post(basePath + "/createWithList")
                    .then()
                    .spec(restAssuredManager.getDefaultResponseSpec())
                    .extract().response();
            
            long responseTime = System.currentTimeMillis() - startTime;
            
            // Log API call
            reportManager.logApiCall(testName, "POST", basePath + "/createWithList", requestBody, 
                    response.getBody().asString(), response.getStatusCode(), responseTime);
            
            if (response.getStatusCode() == 200) {
                logger.info("Users created successfully with list: {}", users.size());
            } else {
                logger.error("Failed to create users with list. Status: {}", response.getStatusCode());
            }
            
            return response;
            
        } catch (JsonProcessingException e) {
            logger.error("Error serializing users list", e);
            return null;
        }
    }
    
    /**
     * Get user by username
     */
    public User getUserByUsername(String username) {
        String testName = "GetUserByUsername_" + username;
        long startTime = System.currentTimeMillis();
        
        Response response = given()
                .spec(restAssuredManager.getDefaultRequestSpec())
                .pathParam("username", username)
                .when()
                .get(basePath + "/{username}")
                .then()
                .spec(restAssuredManager.getDefaultResponseSpec())
                .extract().response();
        
        long responseTime = System.currentTimeMillis() - startTime;
        
        // Log API call
        reportManager.logApiCall(testName, "GET", basePath + "/" + username, null, 
                response.getBody().asString(), response.getStatusCode(), responseTime);
        
        if (response.getStatusCode() == 200) {
            User user = response.as(User.class);
            logger.info("User retrieved successfully: {}", user.getUsername());
            return user;
        } else {
            logger.error("Failed to get user. Status: {}", response.getStatusCode());
            return null;
        }
    }
    
    /**
     * Get user by username and return response
     */
    public Response getUserByUsernameResponse(String username) {
        String testName = "GetUserByUsernameResponse_" + username;
        long startTime = System.currentTimeMillis();
        
        Response response = given()
                .spec(restAssuredManager.getDefaultRequestSpec())
                .pathParam("username", username)
                .when()
                .get(basePath + "/{username}")
                .then()
                .spec(restAssuredManager.getDefaultResponseSpec())
                .extract().response();
        
        long responseTime = System.currentTimeMillis() - startTime;
        
        // Log API call
        reportManager.logApiCall(testName, "GET", basePath + "/" + username, null, 
                response.getBody().asString(), response.getStatusCode(), responseTime);
        
        return response;
    }
    
    /**
     * Update user by username
     */
    public Response updateUser(String username, User user) {
        String testName = "UpdateUser_" + username;
        long startTime = System.currentTimeMillis();
        
        try {
            String requestBody = objectMapper.writeValueAsString(user);
            
            Response response = given()
                    .spec(restAssuredManager.getDefaultRequestSpec())
                    .pathParam("username", username)
                    .body(requestBody)
                    .when()
                    .put(basePath + "/{username}")
                    .then()
                    .spec(restAssuredManager.getDefaultResponseSpec())
                    .extract().response();
            
            long responseTime = System.currentTimeMillis() - startTime;
            
            // Log API call
            reportManager.logApiCall(testName, "PUT", basePath + "/" + username, requestBody, 
                    response.getBody().asString(), response.getStatusCode(), responseTime);
            
            if (response.getStatusCode() == 200) {
                logger.info("User updated successfully: {}", username);
            } else {
                logger.error("Failed to update user. Status: {}", response.getStatusCode());
            }
            
            return response;
            
        } catch (JsonProcessingException e) {
            logger.error("Error serializing user object", e);
            return null;
        }
    }
    
    /**
     * Delete user by username
     */
    public Response deleteUser(String username) {
        String testName = "DeleteUser_" + username;
        long startTime = System.currentTimeMillis();
        
        Response response = given()
                .spec(restAssuredManager.getDefaultRequestSpec())
                .pathParam("username", username)
                .when()
                .delete(basePath + "/{username}")
                .then()
                .spec(restAssuredManager.getDefaultResponseSpec())
                .extract().response();
        
        long responseTime = System.currentTimeMillis() - startTime;
        
        // Log API call
        reportManager.logApiCall(testName, "DELETE", basePath + "/" + username, null, 
                response.getBody().asString(), response.getStatusCode(), responseTime);
        
        if (response.getStatusCode() == 200) {
            logger.info("User deleted successfully: {}", username);
        } else {
            logger.error("Failed to delete user. Status: {}", response.getStatusCode());
        }
        
        return response;
    }
    
    /**
     * User login
     */
    public Response login(String username, String password) {
        String testName = "UserLogin_" + username;
        long startTime = System.currentTimeMillis();
        
        Response response = given()
                .spec(restAssuredManager.getDefaultRequestSpec())
                .queryParam("username", username)
                .queryParam("password", password)
                .when()
                .get(basePath + "/login")
                .then()
                .spec(restAssuredManager.getDefaultResponseSpec())
                .extract().response();
        
        long responseTime = System.currentTimeMillis() - startTime;
        
        // Log API call
        reportManager.logApiCall(testName, "GET", basePath + "/login?username=" + username + "&password=" + password, null, 
                response.getBody().asString(), response.getStatusCode(), responseTime);
        
        if (response.getStatusCode() == 200) {
            logger.info("User login successful: {}", username);
        } else {
            logger.error("User login failed. Status: {}", response.getStatusCode());
        }
        
        return response;
    }
    
    /**
     * User logout
     */
    public Response logout() {
        String testName = "UserLogout";
        long startTime = System.currentTimeMillis();
        
        Response response = given()
                .spec(restAssuredManager.getDefaultRequestSpec())
                .when()
                .get(basePath + "/logout")
                .then()
                .spec(restAssuredManager.getDefaultResponseSpec())
                .extract().response();
        
        long responseTime = System.currentTimeMillis() - startTime;
        
        // Log API call
        reportManager.logApiCall(testName, "GET", basePath + "/logout", null, 
                response.getBody().asString(), response.getStatusCode(), responseTime);
        
        if (response.getStatusCode() == 200) {
            logger.info("User logout successful");
        } else {
            logger.error("User logout failed. Status: {}", response.getStatusCode());
        }
        
        return response;
    }
} 