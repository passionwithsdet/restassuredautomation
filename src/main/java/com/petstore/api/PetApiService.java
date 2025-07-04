package com.petstore.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petstore.framework.config.TestConfig;
import com.petstore.framework.core.RestAssuredManager;
import com.petstore.framework.reporting.ReportManager;
import com.petstore.models.Pet;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Pet API Service for PetStore API operations
 * Handles CRUD operations for Pet entities
 */
public class PetApiService {
    private static final Logger logger = LogManager.getLogger(PetApiService.class);
    private final RestAssuredManager restAssuredManager;
    private final ReportManager reportManager;
    private final ObjectMapper objectMapper;
    private final String basePath = "/pet";
    
    public PetApiService() {
        this.restAssuredManager = RestAssuredManager.getInstance();
        this.reportManager = ReportManager.getInstance();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Create a new pet
     */
    public Pet createPet(Pet pet) {
        String testName = "CreatePet_" + pet.getName();
        long startTime = System.currentTimeMillis();
        
        try {
            String requestBody = objectMapper.writeValueAsString(pet);
            
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
                Pet createdPet = response.as(Pet.class);
                logger.info("Pet created successfully: {}", createdPet.getId());
                return createdPet;
            } else {
                logger.error("Failed to create pet. Status: {}", response.getStatusCode());
                return null;
            }
            
        } catch (JsonProcessingException e) {
            logger.error("Error serializing pet object", e);
            return null;
        }
    }
    
    /**
     * Get pet by ID
     */
    public Pet getPetById(Long petId) {
        String testName = "GetPetById_" + petId;
        long startTime = System.currentTimeMillis();
        
        Response response = given()
                .spec(restAssuredManager.getDefaultRequestSpec())
                .pathParam("petId", petId)
                .when()
                .get(basePath + "/{petId}")
                .then()
                .spec(restAssuredManager.getDefaultResponseSpec())
                .extract().response();
        
        long responseTime = System.currentTimeMillis() - startTime;
        
        // Log API call
        reportManager.logApiCall(testName, "GET", basePath + "/" + petId, null, 
                response.getBody().asString(), response.getStatusCode(), responseTime);
        
        if (response.getStatusCode() == 200) {
            Pet pet = response.as(Pet.class);
            logger.info("Pet retrieved successfully: {}", pet.getName());
            return pet;
        } else {
            logger.error("Failed to get pet. Status: {}", response.getStatusCode());
            return null;
        }
    }
    
    /**
     * Get pet by ID and return response
     */
    public Response getPetByIdResponse(Long petId) {
        String testName = "GetPetByIdResponse_" + petId;
        long startTime = System.currentTimeMillis();
        
        Response response = given()
                .spec(restAssuredManager.getDefaultRequestSpec())
                .pathParam("petId", petId)
                .when()
                .get(basePath + "/{petId}")
                .then()
                .spec(restAssuredManager.getDefaultResponseSpec())
                .extract().response();
        
        long responseTime = System.currentTimeMillis() - startTime;
        
        // Log API call
        reportManager.logApiCall(testName, "GET", basePath + "/" + petId, null, 
                response.getBody().asString(), response.getStatusCode(), responseTime);
        
        return response;
    }
    
    /**
     * Update pet
     */
    public Pet updatePet(Pet pet) {
        String testName = "UpdatePet_" + pet.getId();
        long startTime = System.currentTimeMillis();
        
        try {
            String requestBody = objectMapper.writeValueAsString(pet);
            
            Response response = given()
                    .spec(restAssuredManager.getDefaultRequestSpec())
                    .body(requestBody)
                    .when()
                    .put(basePath)
                    .then()
                    .spec(restAssuredManager.getDefaultResponseSpec())
                    .extract().response();
            
            long responseTime = System.currentTimeMillis() - startTime;
            
            // Log API call
            reportManager.logApiCall(testName, "PUT", basePath, requestBody, 
                    response.getBody().asString(), response.getStatusCode(), responseTime);
            
            if (response.getStatusCode() == 200) {
                Pet updatedPet = response.as(Pet.class);
                logger.info("Pet updated successfully: {}", updatedPet.getId());
                return updatedPet;
            } else {
                logger.error("Failed to update pet. Status: {}", response.getStatusCode());
                return null;
            }
            
        } catch (JsonProcessingException e) {
            logger.error("Error serializing pet object", e);
            return null;
        }
    }
    
    /**
     * Delete pet by ID
     */
    public boolean deletePet(Long petId) {
        String testName = "DeletePet_" + petId;
        long startTime = System.currentTimeMillis();
        
        Response response = given()
                .spec(restAssuredManager.getDefaultRequestSpec())
                .pathParam("petId", petId)
                .when()
                .delete(basePath + "/{petId}")
                .then()
                .spec(restAssuredManager.getDefaultResponseSpec())
                .extract().response();
        
        long responseTime = System.currentTimeMillis() - startTime;
        
        // Log API call
        reportManager.logApiCall(testName, "DELETE", basePath + "/" + petId, null, 
                response.getBody().asString(), response.getStatusCode(), responseTime);
        
        if (response.getStatusCode() == 200) {
            logger.info("Pet deleted successfully: {}", petId);
            return true;
        } else {
            logger.error("Failed to delete pet. Status: {}", response.getStatusCode());
            return false;
        }
    }
    
    /**
     * Delete pet by ID and return response
     */
    public Response deletePetResponse(Long petId) {
        String testName = "DeletePetResponse_" + petId;
        long startTime = System.currentTimeMillis();
        
        Response response = given()
                .spec(restAssuredManager.getDefaultRequestSpec())
                .pathParam("petId", petId)
                .when()
                .delete(basePath + "/{petId}")
                .then()
                .spec(restAssuredManager.getDefaultResponseSpec())
                .extract().response();
        
        long responseTime = System.currentTimeMillis() - startTime;
        
        // Log API call
        reportManager.logApiCall(testName, "DELETE", basePath + "/" + petId, null, 
                response.getBody().asString(), response.getStatusCode(), responseTime);
        
        if (response.getStatusCode() == 200) {
            logger.info("Pet deleted successfully: {}", petId);
        } else {
            logger.error("Failed to delete pet. Status: {}", response.getStatusCode());
        }
        
        return response;
    }
    
    /**
     * Find pets by status
     */
    public List<Pet> findPetsByStatus(String status) {
        String testName = "FindPetsByStatus_" + status;
        long startTime = System.currentTimeMillis();
        
        Response response = given()
                .spec(restAssuredManager.getDefaultRequestSpec())
                .queryParam("status", status)
                .when()
                .get(basePath + "/findByStatus")
                .then()
                .spec(restAssuredManager.getDefaultResponseSpec())
                .extract().response();
        
        long responseTime = System.currentTimeMillis() - startTime;
        
        // Log API call
        reportManager.logApiCall(testName, "GET", basePath + "/findByStatus?status=" + status, null, 
                response.getBody().asString(), response.getStatusCode(), responseTime);
        
        if (response.getStatusCode() == 200) {
            List<Pet> pets = response.jsonPath().getList("", Pet.class);
            logger.info("Found {} pets with status: {}", pets.size(), status);
            return pets;
        } else {
            logger.error("Failed to find pets by status. Status: {}", response.getStatusCode());
            return null;
        }
    }
    
    /**
     * Update pet with form data
     */
    public boolean updatePetWithForm(Long petId, String name, String status) {
        String testName = "UpdatePetWithForm_" + petId;
        long startTime = System.currentTimeMillis();
        
        Response response = given()
                .spec(restAssuredManager.getFormDataRequestSpec())
                .pathParam("petId", petId)
                .formParam("name", name)
                .formParam("status", status)
                .when()
                .post(basePath + "/{petId}")
                .then()
                .spec(restAssuredManager.getDefaultResponseSpec())
                .extract().response();
        
        long responseTime = System.currentTimeMillis() - startTime;
        
        // Log API call
        String formData = "name=" + name + "&status=" + status;
        reportManager.logApiCall(testName, "POST", basePath + "/" + petId, formData, 
                response.getBody().asString(), response.getStatusCode(), responseTime);
        
        if (response.getStatusCode() == 200) {
            logger.info("Pet updated with form successfully: {}", petId);
            return true;
        } else {
            logger.error("Failed to update pet with form. Status: {}", response.getStatusCode());
            return false;
        }
    }
    
    /**
     * Update pet with form data and return response
     */
    public Response updatePetWithFormResponse(Long petId, String name, String status) {
        String testName = "UpdatePetWithFormResponse_" + petId;
        long startTime = System.currentTimeMillis();
        
        Response response = given()
                .spec(restAssuredManager.getFormDataRequestSpec())
                .pathParam("petId", petId)
                .formParam("name", name)
                .formParam("status", status)
                .when()
                .post(basePath + "/{petId}")
                .then()
                .spec(restAssuredManager.getDefaultResponseSpec())
                .extract().response();
        
        long responseTime = System.currentTimeMillis() - startTime;
        
        // Log API call
        String formData = "name=" + name + "&status=" + status;
        reportManager.logApiCall(testName, "POST", basePath + "/" + petId, formData, 
                response.getBody().asString(), response.getStatusCode(), responseTime);
        
        if (response.getStatusCode() == 200) {
            logger.info("Pet updated with form successfully: {}", petId);
        } else {
            logger.error("Failed to update pet with form. Status: {}", response.getStatusCode());
        }
        
        return response;
    }
    
    /**
     * Upload pet image
     */
    public boolean uploadPetImage(Long petId, String additionalMetadata, String filePath) {
        String testName = "UploadPetImage_" + petId;
        long startTime = System.currentTimeMillis();
        
        Response response = given()
                .spec(restAssuredManager.getFileUploadRequestSpec())
                .pathParam("petId", petId)
                .formParam("additionalMetadata", additionalMetadata)
                .multiPart("file", new java.io.File(filePath))
                .when()
                .post(basePath + "/{petId}/uploadImage")
                .then()
                .spec(restAssuredManager.getDefaultResponseSpec())
                .extract().response();
        
        long responseTime = System.currentTimeMillis() - startTime;
        
        // Log API call
        reportManager.logApiCall(testName, "POST", basePath + "/" + petId + "/uploadImage", 
                "File: " + filePath, response.getBody().asString(), response.getStatusCode(), responseTime);
        
        if (response.getStatusCode() == 200) {
            logger.info("Pet image uploaded successfully: {}", petId);
            return true;
        } else {
            logger.error("Failed to upload pet image. Status: {}", response.getStatusCode());
            return false;
        }
    }
    
    /**
     * Upload pet image and return response
     */
    public Response uploadPetImageResponse(Long petId, String additionalMetadata, String filePath) {
        String testName = "UploadPetImageResponse_" + petId;
        long startTime = System.currentTimeMillis();
        
        Response response = given()
                .spec(restAssuredManager.getFileUploadRequestSpec())
                .pathParam("petId", petId)
                .formParam("additionalMetadata", additionalMetadata)
                .multiPart("file", new java.io.File(filePath))
                .when()
                .post(basePath + "/{petId}/uploadImage")
                .then()
                .spec(restAssuredManager.getDefaultResponseSpec())
                .extract().response();
        
        long responseTime = System.currentTimeMillis() - startTime;
        
        // Log API call
        reportManager.logApiCall(testName, "POST", basePath + "/" + petId + "/uploadImage", 
                "File: " + filePath, response.getBody().asString(), response.getStatusCode(), responseTime);
        
        if (response.getStatusCode() == 200) {
            logger.info("Pet image uploaded successfully: {}", petId);
        } else {
            logger.error("Failed to upload pet image. Status: {}", response.getStatusCode());
        }
        
        return response;
    }
    
    /**
     * Get pet inventory by status
     */
    public Map<String, Integer> getPetInventory() {
        String testName = "GetPetInventory";
        long startTime = System.currentTimeMillis();
        
        Response response = given()
                .spec(restAssuredManager.getAuthenticatedRequestSpec())
                .when()
                .get("/store/inventory")
                .then()
                .spec(restAssuredManager.getDefaultResponseSpec())
                .extract().response();
        
        long responseTime = System.currentTimeMillis() - startTime;
        
        // Log API call
        reportManager.logApiCall(testName, "GET", "/store/inventory", null, 
                response.getBody().asString(), response.getStatusCode(), responseTime);
        
        if (response.getStatusCode() == 200) {
            Map<String, Integer> inventory = response.as(Map.class);
            logger.info("Pet inventory retrieved successfully");
            return inventory;
        } else {
            logger.error("Failed to get pet inventory. Status: {}", response.getStatusCode());
            return null;
        }
    }
} 