package com.petstore.tests;

import com.petstore.api.PetApiService;
import com.petstore.framework.config.TestConfig;
import com.petstore.models.Pet;
import com.petstore.models.Category;
import com.petstore.models.Tag;
import com.petstore.framework.reporting.CustomReportUtility;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;

/**
 * Example test class demonstrating custom reporting functionality
 * Shows how to integrate custom reporting with API tests
 */
public class CustomReportExampleTest {
    private static final Logger logger = LogManager.getLogger(CustomReportExampleTest.class);
    private PetApiService petApiService;
    private TestConfig config;
    
    @BeforeClass
    public void setUp() {
        config = TestConfig.getInstance();
        petApiService = new PetApiService();
        
        // Add environment information to custom report
        CustomReportUtility.addMetric("Environment", config.getEnvironment());
        CustomReportUtility.addMetric("Base URL", config.getBaseUrl());
        CustomReportUtility.addMetric("Test Suite", "Custom Report Example Tests");
        
        logger.info("Custom report example test setup completed");
    }
    
    @Test(description = "Create a new pet with custom reporting")
    public void testCreatePetWithCustomReporting() {
        String testName = "Create Pet with Custom Reporting";
        String testClass = this.getClass().getSimpleName();
        
        try {
            // Start custom test tracking
            CustomReportUtility.startCustomTest(testName, testClass);
            
            // Log test step
            CustomReportUtility.logStep("Setup", "Creating pet data for API test");
            
            // Create test data
            Category category = new Category(1L, "Dogs");
            List<Tag> tags = Arrays.asList(
                new Tag(1L, "friendly"),
                new Tag(2L, "playful")
            );
            
            Pet pet = new Pet();
            pet.setId(12345L);
            pet.setName("Buddy");
            pet.setCategory(category);
            pet.setPhotoUrls(Arrays.asList("https://example.com/buddy.jpg"));
            pet.setTags(tags);
            pet.setStatus("available");
            
            // Log test data
            CustomReportUtility.logTestData("Pet ID", pet.getId());
            CustomReportUtility.logTestData("Pet Name", pet.getName());
            CustomReportUtility.logTestData("Pet Status", pet.getStatus());
            
            // Log API request
            CustomReportUtility.logApiRequest("POST", "/pet", pet.toString());
            
            // Make API call
            Pet createdPet = petApiService.createPet(pet);
            
            // Log API response
            CustomReportUtility.logApiResponse("POST", "/pet", createdPet != null ? createdPet.toString() : "null", 200);
            
            // Add API call to custom report
            CustomReportUtility.addApiCall("POST", "/pet", pet.toString(), createdPet != null ? createdPet.toString() : "null", 200);
            
            // Log validation step
            CustomReportUtility.logStep("Validation", "Validating API response");
            
            // Validate response
            Assert.assertNotNull(createdPet, "Created pet should not be null");
            Assert.assertEquals(createdPet.getName(), "Buddy", "Pet name should match");
            Assert.assertEquals(createdPet.getStatus(), "available", "Pet status should match");
            
            // Log validation results
            CustomReportUtility.logValidation("Pet Name", "Buddy", createdPet.getName(), "Buddy".equals(createdPet.getName()));
            CustomReportUtility.logValidation("Pet Status", "available", createdPet.getStatus(), "available".equals(createdPet.getStatus()));
            
            // Log success
            CustomReportUtility.logStep("Result", "Pet created successfully");
            
            // End test with success
            CustomReportUtility.endCustomTest("PASSED");
            
        } catch (Exception e) {
            // Log error details
            CustomReportUtility.addError(e);
            CustomReportUtility.logStep("Error", "Test failed with exception: " + e.getMessage());
            
            // End test with failure
            CustomReportUtility.endCustomTest("FAILED");
            
            // Re-throw for TestNG
            throw e;
        }
    }
    
    @Test(description = "Get pet by ID with custom reporting")
    public void testGetPetByIdWithCustomReporting() {
        String testName = "Get Pet by ID with Custom Reporting";
        String testClass = this.getClass().getSimpleName();
        
        try {
            // Start custom test tracking
            CustomReportUtility.startCustomTest(testName, testClass);
            
            // Log test step
            CustomReportUtility.logStep("Setup", "Preparing to get pet by ID");
            
            Long petId = 12345L;
            CustomReportUtility.logTestData("Pet ID to Retrieve", petId);
            
            // Log API request
            CustomReportUtility.logApiRequest("GET", "/pet/" + petId, null);
            
            // Make API call
            Pet retrievedPet = petApiService.getPetById(petId);
            
            // Log API response
            CustomReportUtility.logApiResponse("GET", "/pet/" + petId, retrievedPet != null ? retrievedPet.toString() : "null", retrievedPet != null ? 200 : 404);
            
            // Add API call to custom report
            CustomReportUtility.addApiCall("GET", "/pet/" + petId, null, retrievedPet != null ? retrievedPet.toString() : "null", retrievedPet != null ? 200 : 404);
            
            // Log validation step
            CustomReportUtility.logStep("Validation", "Validating pet retrieval response");
            
            // Validate response
            Assert.assertNotNull(retrievedPet, "Retrieved pet should not be null");
            Assert.assertEquals(retrievedPet.getId(), petId, "Pet ID should match");
            
            // Log validation results
            CustomReportUtility.logValidation("Pet ID", petId.toString(), retrievedPet.getId().toString(), petId.equals(retrievedPet.getId()));
            
            // Log success
            CustomReportUtility.logStep("Result", "Pet retrieved successfully");
            
            // End test with success
            CustomReportUtility.endCustomTest("PASSED");
            
        } catch (Exception e) {
            // Log error details
            CustomReportUtility.addError(e);
            CustomReportUtility.logStep("Error", "Test failed with exception: " + e.getMessage());
            
            // End test with failure
            CustomReportUtility.endCustomTest("FAILED");
            
            // Re-throw for TestNG
            throw e;
        }
    }
    
    @Test(description = "Update pet status with custom reporting")
    public void testUpdatePetStatusWithCustomReporting() {
        String testName = "Update Pet Status with Custom Reporting";
        String testClass = this.getClass().getSimpleName();
        
        try {
            // Start custom test tracking
            CustomReportUtility.startCustomTest(testName, testClass);
            
            // Log test step
            CustomReportUtility.logStep("Setup", "Preparing to update pet status");
            
            Long petId = 12345L;
            String newStatus = "sold";
            
            CustomReportUtility.logTestData("Pet ID", petId);
            CustomReportUtility.logTestData("New Status", newStatus);
            
            // Log API request
            CustomReportUtility.logApiRequest("PUT", "/pet", "{\"id\":" + petId + ",\"status\":\"" + newStatus + "\"}");
            
            // Create a pet first to update
            Pet petToUpdate = createTestPet("TestPet", "available");
            Pet createdPet = petApiService.createPet(petToUpdate);
            Assert.assertNotNull(createdPet, "Pet should be created for update test");
            
            // Update the pet status
            createdPet.setStatus(newStatus);
            Pet updatedPet = petApiService.updatePet(createdPet);
            
            // Log API response
            CustomReportUtility.logApiResponse("PUT", "/pet", updatedPet != null ? updatedPet.toString() : "null", updatedPet != null ? 200 : 400);
            
            // Add API call to custom report
            CustomReportUtility.addApiCall("PUT", "/pet", createdPet.toString(), updatedPet != null ? updatedPet.toString() : "null", updatedPet != null ? 200 : 400);
            
            // Log validation step
            CustomReportUtility.logStep("Validation", "Validating pet status update");
            
            // Validate response
            Assert.assertNotNull(updatedPet, "Updated pet should not be null");
            Assert.assertEquals(updatedPet.getStatus(), newStatus, "Pet status should be updated");
            
            // Log validation results
            CustomReportUtility.logValidation("Pet Status Updated", newStatus, updatedPet.getStatus(), newStatus.equals(updatedPet.getStatus()));
            
            // Log success
            CustomReportUtility.logStep("Result", "Pet status updated successfully");
            
            // End test with success
            CustomReportUtility.endCustomTest("PASSED");
            
        } catch (Exception e) {
            // Log error details
            CustomReportUtility.addError(e);
            CustomReportUtility.logStep("Error", "Test failed with exception: " + e.getMessage());
            
            // End test with failure
            CustomReportUtility.endCustomTest("FAILED");
            
            // Re-throw for TestNG
            throw e;
        }
    }
    
    @Test(description = "Delete pet with custom reporting")
    public void testDeletePetWithCustomReporting() {
        String testName = "Delete Pet with Custom Reporting";
        String testClass = this.getClass().getSimpleName();
        
        try {
            // Start custom test tracking
            CustomReportUtility.startCustomTest(testName, testClass);
            
            // Log test step
            CustomReportUtility.logStep("Setup", "Preparing to delete pet");
            
            Long petId = 12345L;
            CustomReportUtility.logTestData("Pet ID to Delete", petId);
            
            // Log API request
            CustomReportUtility.logApiRequest("DELETE", "/pet/" + petId, null);
            
            // Create a pet first to delete
            Pet petToDelete = createTestPet("PetToDelete", "available");
            Pet createdPet = petApiService.createPet(petToDelete);
            Assert.assertNotNull(createdPet, "Pet should be created for delete test");
            
            // Make API call
            boolean deleted = petApiService.deletePet(createdPet.getId());
            
            // Log API response
            CustomReportUtility.logApiResponse("DELETE", "/pet/" + createdPet.getId(), deleted ? "Success" : "Failed", deleted ? 200 : 400);
            
            // Add API call to custom report
            CustomReportUtility.addApiCall("DELETE", "/pet/" + createdPet.getId(), null, deleted ? "Success" : "Failed", deleted ? 200 : 400);
            
            // Log validation step
            CustomReportUtility.logStep("Validation", "Validating pet deletion");
            
            // Validate response
            Assert.assertTrue(deleted, "Pet should be deleted successfully");
            
            // Verify deletion by trying to get the pet (should return null)
            Pet retrievedPet = petApiService.getPetById(createdPet.getId());
            Assert.assertNull(retrievedPet, "Pet should not exist after deletion");
            
            // Log validation results
            CustomReportUtility.logValidation("Pet Deleted", "true", String.valueOf(deleted), deleted);
            CustomReportUtility.logValidation("Pet Not Found After Delete", "null", retrievedPet != null ? "not null" : "null", retrievedPet == null);
            
            // Log success
            CustomReportUtility.logStep("Result", "Pet deleted successfully");
            
            // End test with success
            CustomReportUtility.endCustomTest("PASSED");
            
        } catch (Exception e) {
            // Log error details
            CustomReportUtility.addError(e);
            CustomReportUtility.logStep("Error", "Test failed with exception: " + e.getMessage());
            
            // End test with failure
            CustomReportUtility.endCustomTest("FAILED");
            
            // Re-throw for TestNG
            throw e;
        }
    }
    
    /**
     * Helper method to create test pet data
     */
    private Pet createTestPet(String name, String status) {
        Category category = new Category(1L, "Dogs");
        List<Tag> tags = Arrays.asList(
            new Tag(1L, "friendly"),
            new Tag(2L, "playful")
        );
        
        Pet pet = new Pet();
        pet.setName(name);
        pet.setCategory(category);
        pet.setPhotoUrls(Arrays.asList("https://example.com/" + name.toLowerCase() + ".jpg"));
        pet.setTags(tags);
        pet.setStatus(status);
        
        return pet;
    }
} 