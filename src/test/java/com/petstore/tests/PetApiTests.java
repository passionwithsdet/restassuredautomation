package com.petstore.tests;

import com.petstore.api.PetApiService;
import com.petstore.framework.data.DataProvider;
import com.petstore.framework.data.MongoDataProvider;
import com.petstore.framework.reporting.ReportManager;
import com.petstore.models.Category;
import com.petstore.models.Pet;
import com.petstore.models.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.*;

/**
 * Comprehensive Pet API Test Suite
 * Tests all CRUD operations and edge cases for Pet API
 */
public class PetApiTests {
    private static final Logger logger = LogManager.getLogger(PetApiTests.class);
    private PetApiService petApiService;
    private ReportManager reportManager;
    private MongoDataProvider mongoDataProvider;
    private List<Long> createdPetIds;
    
    @BeforeClass
    public void setUp() {
        petApiService = new PetApiService();
        reportManager = ReportManager.getInstance();
        mongoDataProvider = new MongoDataProvider();
        createdPetIds = new ArrayList<>();
        
        // Initialize MongoDB test data
        mongoDataProvider.initializeTestData();
        
        logger.info("Pet API Tests setup completed");
    }
    
    @AfterClass
    public void tearDown() {
        // Clean up created pets
        for (Long petId : createdPetIds) {
            try {
                petApiService.deletePet(petId);
                logger.info("Cleaned up pet with ID: {}", petId);
            } catch (Exception e) {
                logger.warn("Failed to clean up pet with ID: {}", petId, e);
            }
        }
        
        // Clean up MongoDB test data
        mongoDataProvider.cleanupTestData();
        mongoDataProvider.closeConnection();
        
        logger.info("Pet API Tests cleanup completed");
    }
    
    @Test(description = "Create a new pet with valid data")
    public void testCreatePet() {
        String testName = "testCreatePet";
        reportManager.startTest(testName, "Create a new pet with valid data");
        
        // Create test data
        Pet pet = createTestPet("Fluffy", "available");
        
        // Execute test
        Pet createdPet = petApiService.createPet(pet);
        
        // Assertions
        Assert.assertNotNull(createdPet, "Created pet should not be null");
        Assert.assertNotNull(createdPet.getId(), "Pet ID should not be null");
        Assert.assertEquals(createdPet.getName(), pet.getName(), "Pet name should match");
        Assert.assertEquals(createdPet.getStatus(), pet.getStatus(), "Pet status should match");
        
        // Store for cleanup
        createdPetIds.add(createdPet.getId());
        
        reportManager.endTest(testName, com.aventstack.extentreports.Status.PASS, "Pet created successfully");
        logger.info("Test passed: Pet created with ID: {}", createdPet.getId());
    }
    
    @Test(description = "Get pet by valid ID")
    public void testGetPetById() {
        String testName = "testGetPetById";
        reportManager.startTest(testName, "Get pet by valid ID");
        
        // Create a pet first
        Pet pet = createTestPet("Buddy", "available");
        Pet createdPet = petApiService.createPet(pet);
        createdPetIds.add(createdPet.getId());
        
        // Execute test
        Pet retrievedPet = petApiService.getPetById(createdPet.getId());
        
        // Assertions
        Assert.assertNotNull(retrievedPet, "Retrieved pet should not be null");
        Assert.assertEquals(retrievedPet.getId(), createdPet.getId(), "Pet ID should match");
        Assert.assertEquals(retrievedPet.getName(), createdPet.getName(), "Pet name should match");
        
        reportManager.endTest(testName, com.aventstack.extentreports.Status.PASS, "Pet retrieved successfully");
        logger.info("Test passed: Pet retrieved with ID: {}", retrievedPet.getId());
    }
    
    @Test(description = "Get pet by invalid ID")
    public void testGetPetByInvalidId() {
        String testName = "testGetPetByInvalidId";
        reportManager.startTest(testName, "Get pet by invalid ID");
        
        // Execute test with invalid ID
        Pet retrievedPet = petApiService.getPetById(999999L);
        
        // Assertions
        Assert.assertNull(retrievedPet, "Pet should be null for invalid ID");
        
        reportManager.endTest(testName, com.aventstack.extentreports.Status.PASS, "Invalid pet ID handled correctly");
        logger.info("Test passed: Invalid pet ID handled correctly");
    }
    
    @Test(description = "Update pet with valid data")
    public void testUpdatePet() {
        String testName = "testUpdatePet";
        reportManager.startTest(testName, "Update pet with valid data");
        
        // Create a pet first
        Pet pet = createTestPet("Max", "available");
        Pet createdPet = petApiService.createPet(pet);
        createdPetIds.add(createdPet.getId());
        
        // Update pet data
        createdPet.setName("Max Updated");
        createdPet.setStatus("pending");
        
        // Execute test
        Pet updatedPet = petApiService.updatePet(createdPet);
        
        // Assertions
        Assert.assertNotNull(updatedPet, "Updated pet should not be null");
        Assert.assertEquals(updatedPet.getName(), "Max Updated", "Pet name should be updated");
        Assert.assertEquals(updatedPet.getStatus(), "pending", "Pet status should be updated");
        
        reportManager.endTest(testName, com.aventstack.extentreports.Status.PASS, "Pet updated successfully");
        logger.info("Test passed: Pet updated successfully");
    }
    
    @Test(description = "Delete pet by valid ID")
    public void testDeletePet() {
        String testName = "testDeletePet";
        reportManager.startTest(testName, "Delete pet by valid ID");
        
        // Create a pet first
        Pet pet = createTestPet("Rex", "available");
        Pet createdPet = petApiService.createPet(pet);
        
        // Execute test
        boolean deleted = petApiService.deletePet(createdPet.getId());
        
        // Assertions
        Assert.assertTrue(deleted, "Pet should be deleted successfully");
        
        // Verify pet is deleted
        Pet retrievedPet = petApiService.getPetById(createdPet.getId());
        Assert.assertNull(retrievedPet, "Pet should not exist after deletion");
        
        reportManager.endTest(testName, com.aventstack.extentreports.Status.PASS, "Pet deleted successfully");
        logger.info("Test passed: Pet deleted successfully");
    }
    
    @Test(description = "Find pets by status - available")
    public void testFindPetsByStatusAvailable() {
        String testName = "testFindPetsByStatusAvailable";
        reportManager.startTest(testName, "Find pets by status - available");
        
        // Execute test
        List<Pet> pets = petApiService.findPetsByStatus("available");
        
        // Assertions
        Assert.assertNotNull(pets, "Pets list should not be null");
        Assert.assertTrue(pets.size() >= 0, "Pets list should be non-negative");
        
        // Verify all pets have correct status
        for (Pet pet : pets) {
            Assert.assertEquals(pet.getStatus(), "available", "All pets should have 'available' status");
        }
        
        reportManager.endTest(testName, com.aventstack.extentreports.Status.PASS, 
                "Found " + pets.size() + " available pets");
        logger.info("Test passed: Found {} available pets", pets.size());
    }
    
    @Test(description = "Find pets by status - pending")
    public void testFindPetsByStatusPending() {
        String testName = "testFindPetsByStatusPending";
        reportManager.startTest(testName, "Find pets by status - pending");
        
        // Execute test
        List<Pet> pets = petApiService.findPetsByStatus("pending");
        
        // Assertions
        Assert.assertNotNull(pets, "Pets list should not be null");
        
        // Verify all pets have correct status
        for (Pet pet : pets) {
            Assert.assertEquals(pet.getStatus(), "pending", "All pets should have 'pending' status");
        }
        
        reportManager.endTest(testName, com.aventstack.extentreports.Status.PASS, 
                "Found " + pets.size() + " pending pets");
        logger.info("Test passed: Found {} pending pets", pets.size());
    }
    
    @Test(description = "Find pets by status - sold")
    public void testFindPetsByStatusSold() {
        String testName = "testFindPetsByStatusSold";
        reportManager.startTest(testName, "Find pets by status - sold");
        
        // Execute test
        List<Pet> pets = petApiService.findPetsByStatus("sold");
        
        // Assertions
        Assert.assertNotNull(pets, "Pets list should not be null");
        
        // Verify all pets have correct status
        for (Pet pet : pets) {
            Assert.assertEquals(pet.getStatus(), "sold", "All pets should have 'sold' status");
        }
        
        reportManager.endTest(testName, com.aventstack.extentreports.Status.PASS, 
                "Found " + pets.size() + " sold pets");
        logger.info("Test passed: Found {} sold pets", pets.size());
    }
    
    @Test(description = "Update pet with form data")
    public void testUpdatePetWithForm() {
        String testName = "testUpdatePetWithForm";
        reportManager.startTest(testName, "Update pet with form data");
        
        // Create a pet first
        Pet pet = createTestPet("Luna", "available");
        Pet createdPet = petApiService.createPet(pet);
        createdPetIds.add(createdPet.getId());
        
        // Execute test
        boolean updated = petApiService.updatePetWithForm(createdPet.getId(), "Luna Updated", "sold");
        
        // Assertions
        Assert.assertTrue(updated, "Pet should be updated successfully with form data");
        
        // Verify the update
        Pet retrievedPet = petApiService.getPetById(createdPet.getId());
        Assert.assertEquals(retrievedPet.getName(), "Luna Updated", "Pet name should be updated");
        Assert.assertEquals(retrievedPet.getStatus(), "sold", "Pet status should be updated");
        
        reportManager.endTest(testName, com.aventstack.extentreports.Status.PASS, "Pet updated with form successfully");
        logger.info("Test passed: Pet updated with form successfully");
    }
    
    @Test(description = "Get pet inventory")
    public void testGetPetInventory() {
        String testName = "testGetPetInventory";
        reportManager.startTest(testName, "Get pet inventory");
        
        // Execute test
        Map<String, Integer> inventory = petApiService.getPetInventory();
        
        // Assertions
        Assert.assertNotNull(inventory, "Inventory should not be null");
        Assert.assertTrue(inventory.size() >= 0, "Inventory should be non-negative");
        
        // Log inventory details
        logger.info("Pet inventory: {}", inventory);
        
        reportManager.endTest(testName, com.aventstack.extentreports.Status.PASS, 
                "Inventory retrieved successfully");
        logger.info("Test passed: Inventory retrieved successfully");
    }
    
    @Test(dataProvider = "petTestData", dataProviderClass = DataProvider.class, 
          description = "Data-driven test for pet creation")
    public void testCreatePetWithData(Map<String, String> testData) {
        String testName = "testCreatePetWithData_" + testData.get("name");
        reportManager.startTest(testName, "Data-driven test for pet creation");
        
        // Log test data
        reportManager.logTestData(testName, new HashMap<>(testData));
        
        // Create pet with test data
        Pet pet = new Pet();
        pet.setName(testData.get("name"));
        pet.setStatus(testData.get("status"));
        
        if (testData.containsKey("category")) {
            Category category = new Category(1L, testData.get("category"));
            pet.setCategory(category);
        }
        
        // Execute test
        Pet createdPet = petApiService.createPet(pet);
        
        // Assertions
        Assert.assertNotNull(createdPet, "Created pet should not be null");
        Assert.assertEquals(createdPet.getName(), testData.get("name"), "Pet name should match test data");
        Assert.assertEquals(createdPet.getStatus(), testData.get("status"), "Pet status should match test data");
        
        // Store for cleanup
        createdPetIds.add(createdPet.getId());
        
        reportManager.endTest(testName, com.aventstack.extentreports.Status.PASS, 
                "Pet created successfully with test data");
        logger.info("Test passed: Pet created with test data - {}", testData.get("name"));
    }
    
    @Test(dataProvider = "mongoPetData", dataProviderClass = MongoDataProvider.class, 
          description = "MongoDB data-driven test for pet creation")
    public void testCreatePetWithMongoData(Map<String, Object> testData) {
        String testName = "testCreatePetWithMongoData_" + testData.get("name");
        reportManager.startTest(testName, "MongoDB data-driven test for pet creation");
        
        // Log test data
        reportManager.logTestData(testName, new HashMap<>(testData));
        
        // Create pet with MongoDB test data
        Pet pet = new Pet();
        pet.setName((String) testData.get("name"));
        pet.setStatus((String) testData.get("status"));
        
        if (testData.containsKey("category")) {
            Category category = new Category(1L, (String) testData.get("category"));
            pet.setCategory(category);
        }
        
        // Execute test
        Pet createdPet = petApiService.createPet(pet);
        
        // Assertions
        Assert.assertNotNull(createdPet, "Created pet should not be null");
        Assert.assertEquals(createdPet.getName(), testData.get("name"), "Pet name should match MongoDB test data");
        Assert.assertEquals(createdPet.getStatus(), testData.get("status"), "Pet status should match MongoDB test data");
        
        // Store for cleanup
        createdPetIds.add(createdPet.getId());
        
        reportManager.endTest(testName, com.aventstack.extentreports.Status.PASS, 
                "Pet created successfully with MongoDB test data");
        logger.info("Test passed: Pet created with MongoDB test data - {}", testData.get("name"));
    }
    
    @Test(description = "Create pet with complete data including category and tags")
    public void testCreatePetWithCompleteData() {
        String testName = "testCreatePetWithCompleteData";
        reportManager.startTest(testName, "Create pet with complete data including category and tags");
        
        // Create complete pet data
        Pet pet = new Pet();
        pet.setName("Complete Pet");
        pet.setStatus("available");
        
        // Add category
        Category category = new Category(1L, "Dogs");
        pet.setCategory(category);
        
        // Add photo URLs
        List<String> photoUrls = Arrays.asList("http://example.com/photo1.jpg", "http://example.com/photo2.jpg");
        pet.setPhotoUrls(photoUrls);
        
        // Add tags
        List<Tag> tags = Arrays.asList(
            new Tag(1L, "friendly"),
            new Tag(2L, "trained")
        );
        pet.setTags(tags);
        
        // Execute test
        Pet createdPet = petApiService.createPet(pet);
        
        // Assertions
        Assert.assertNotNull(createdPet, "Created pet should not be null");
        Assert.assertEquals(createdPet.getName(), "Complete Pet", "Pet name should match");
        Assert.assertEquals(createdPet.getStatus(), "available", "Pet status should match");
        Assert.assertNotNull(createdPet.getCategory(), "Category should not be null");
        Assert.assertEquals(createdPet.getCategory().getName(), "Dogs", "Category name should match");
        Assert.assertEquals(createdPet.getPhotoUrls().size(), 2, "Photo URLs count should match");
        Assert.assertEquals(createdPet.getTags().size(), 2, "Tags count should match");
        
        // Store for cleanup
        createdPetIds.add(createdPet.getId());
        
        reportManager.endTest(testName, com.aventstack.extentreports.Status.PASS, "Pet created with complete data successfully");
        logger.info("Test passed: Pet created with complete data successfully");
    }
    
    /**
     * Helper method to create test pet data
     */
    private Pet createTestPet(String name, String status) {
        Pet pet = new Pet();
        pet.setName(name);
        pet.setStatus(status);
        
        // Add some basic data
        Category category = new Category(1L, "Dogs");
        pet.setCategory(category);
        
        List<String> photoUrls = Arrays.asList("http://example.com/photo.jpg");
        pet.setPhotoUrls(photoUrls);
        
        List<Tag> tags = Arrays.asList(new Tag(1L, "friendly"));
        pet.setTags(tags);
        
        return pet;
    }
} 