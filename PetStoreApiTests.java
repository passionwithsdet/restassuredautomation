package com.petstore.tests;

import com.petstore.api.PetApiService;
import com.petstore.api.StoreApiService;
import com.petstore.api.UserApiService;
import com.petstore.models.Pet;
import com.petstore.models.Category;
import com.petstore.models.Tag;
import com.petstore.models.Order;
import com.petstore.models.User;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Comprehensive PetStore API Test Suite
 * Tests all major endpoints with various scenarios
 */
public class PetStoreApiTests {
    
    private PetApiService petApiService;
    private StoreApiService storeApiService;
    private UserApiService userApiService;
    
    @BeforeClass
    public void setUp() {
        petApiService = new PetApiService();
        storeApiService = new StoreApiService();
        userApiService = new UserApiService();
    }
    
    // ==================== PET API TESTS ====================
    
    @Test(description = "Create a new pet with valid data")
    public void testCreatePet_Success() {
        Pet pet = createSamplePet();
        
        Pet createdPet = petApiService.createPet(pet);
        
        Assert.assertNotNull(createdPet);
        Assert.assertNotNull(createdPet.getId());
        Assert.assertEquals(createdPet.getName(), "Buddy");
        Assert.assertEquals(createdPet.getStatus(), "available");
        Assert.assertNotNull(createdPet.getCategory());
        Assert.assertEquals(createdPet.getCategory().getName(), "Dogs");
    }
    
    @Test(description = "Create pet with minimal required data")
    public void testCreatePet_MinimalData() {
        Pet pet = new Pet();
        pet.setName("Minimal Pet");
        pet.setStatus("available");
        
        Pet createdPet = petApiService.createPet(pet);
        
        Assert.assertNotNull(createdPet);
        Assert.assertNotNull(createdPet.getId());
        Assert.assertEquals(createdPet.getName(), "Minimal Pet");
    }
    
    @Test(description = "Create pet with all fields populated")
    public void testCreatePet_AllFields() {
        Pet pet = createCompletePet();
        
        Pet createdPet = petApiService.createPet(pet);
        
        Assert.assertNotNull(createdPet);
        Assert.assertEquals(createdPet.getName(), "Complete Pet");
        Assert.assertEquals(createdPet.getStatus(), "pending");
        Assert.assertEquals(createdPet.getPhotoUrls().size(), 2);
        Assert.assertEquals(createdPet.getTags().size(), 2);
    }
    
    @Test(description = "Get pet by valid ID")
    public void testGetPetById_Success() {
        // First create a pet
        Pet pet = createSamplePet();
        Pet createdPet = petApiService.createPet(pet);
        
        // Then retrieve it
        Pet retrievedPet = petApiService.getPetById(createdPet.getId());
        
        Assert.assertNotNull(retrievedPet);
        Assert.assertEquals(retrievedPet.getId(), createdPet.getId());
        Assert.assertEquals(retrievedPet.getName(), createdPet.getName());
    }
    
    @Test(description = "Get pet by non-existent ID")
    public void testGetPetById_NotFound() {
        Response response = petApiService.getPetByIdResponse(999999L);
        
        Assert.assertEquals(response.getStatusCode(), 404);
    }
    
    @Test(description = "Update pet with valid data")
    public void testUpdatePet_Success() {
        // Create a pet
        Pet pet = createSamplePet();
        Pet createdPet = petApiService.createPet(pet);
        
        // Update the pet
        createdPet.setName("Updated Buddy");
        createdPet.setStatus("sold");
        
        Pet updatedPet = petApiService.updatePet(createdPet);
        
        Assert.assertEquals(updatedPet.getName(), "Updated Buddy");
        Assert.assertEquals(updatedPet.getStatus(), "sold");
    }
    
    @Test(description = "Delete pet by valid ID")
    public void testDeletePet_Success() {
        // Create a pet
        Pet pet = createSamplePet();
        Pet createdPet = petApiService.createPet(pet);
        
        // Delete the pet
        Response response = petApiService.deletePetResponse(createdPet.getId());
        
        Assert.assertEquals(response.getStatusCode(), 200);
        
        // Verify pet is deleted
        Response getResponse = petApiService.getPetByIdResponse(createdPet.getId());
        Assert.assertEquals(getResponse.getStatusCode(), 404);
    }
    
    @Test(description = "Delete pet by non-existent ID")
    public void testDeletePet_NotFound() {
        Response response = petApiService.deletePetResponse(999999L);
        
        Assert.assertEquals(response.getStatusCode(), 404);
    }
    
    @Test(description = "Find pets by status - available")
    public void testFindPetsByStatus_Available() {
        List<Pet> pets = petApiService.findPetsByStatus("available");
        
        Assert.assertNotNull(pets);
        Assert.assertTrue(pets.size() > 0);
        
        for (Pet pet : pets) {
            Assert.assertEquals(pet.getStatus(), "available");
        }
    }
    
    @Test(description = "Find pets by status - pending")
    public void testFindPetsByStatus_Pending() {
        List<Pet> pets = petApiService.findPetsByStatus("pending");
        
        Assert.assertNotNull(pets);
        
        for (Pet pet : pets) {
            Assert.assertEquals(pet.getStatus(), "pending");
        }
    }
    
    @Test(description = "Find pets by status - sold")
    public void testFindPetsByStatus_Sold() {
        List<Pet> pets = petApiService.findPetsByStatus("sold");
        
        Assert.assertNotNull(pets);
        
        for (Pet pet : pets) {
            Assert.assertEquals(pet.getStatus(), "sold");
        }
    }
    
    @Test(description = "Find pets by multiple statuses")
    public void testFindPetsByStatus_Multiple() {
        List<Pet> pets = petApiService.findPetsByStatus("available,pending");
        
        Assert.assertNotNull(pets);
        
        for (Pet pet : pets) {
            Assert.assertTrue(
                pet.getStatus().equals("available") || 
                pet.getStatus().equals("pending")
            );
        }
    }
    
    @Test(description = "Update pet with form data")
    public void testUpdatePetWithForm() {
        // Create a pet
        Pet pet = createSamplePet();
        Pet createdPet = petApiService.createPet(pet);
        
        // Update using form data
        Response response = petApiService.updatePetWithFormResponse(
            createdPet.getId(), 
            "Form Updated Pet", 
            "sold"
        );
        
        Assert.assertEquals(response.getStatusCode(), 200);
        
        // Verify the update
        Pet updatedPet = petApiService.getPetById(createdPet.getId());
        Assert.assertEquals(updatedPet.getName(), "Form Updated Pet");
        Assert.assertEquals(updatedPet.getStatus(), "sold");
    }
    
    @Test(description = "Upload pet image")
    public void testUploadPetImage() {
        // Create a pet
        Pet pet = createSamplePet();
        Pet createdPet = petApiService.createPet(pet);
        
        // Upload image
        Response response = petApiService.uploadPetImageResponse(
            createdPet.getId(), 
            "Test Image", 
            "src/test/resources/testdata/test-image.jpg"
        );
        
        Assert.assertEquals(response.getStatusCode(), 200);
    }
    
    // ==================== STORE API TESTS ====================
    
    @Test(description = "Get store inventory")
    public void testGetInventory() {
        Map<String, Integer> inventory = storeApiService.getInventory();
        
        Assert.assertNotNull(inventory);
        Assert.assertTrue(inventory.size() > 0);
        
        // Verify inventory has expected statuses
        Assert.assertTrue(inventory.containsKey("available") || 
                         inventory.containsKey("pending") || 
                         inventory.containsKey("sold"));
    }
    
    @Test(description = "Create a new order")
    public void testCreateOrder_Success() {
        Order order = createSampleOrder();
        
        Order createdOrder = storeApiService.createOrder(order);
        
        Assert.assertNotNull(createdOrder);
        Assert.assertNotNull(createdOrder.getId());
        Assert.assertEquals(createdOrder.getPetId(), 1);
        Assert.assertEquals(createdOrder.getQuantity(), 1);
        Assert.assertEquals(createdOrder.getStatus(), "placed");
    }
    
    @Test(description = "Create order with invalid pet ID")
    public void testCreateOrder_InvalidPetId() {
        Order order = createSampleOrder();
        order.setPetId(999999L); // Non-existent pet ID
        
        Response response = storeApiService.createOrderResponse(order);
        
        // PetStore API accepts orders with invalid pet IDs and returns 200
        Assert.assertEquals(response.getStatusCode(), 200);
        
        // Verify the order was created with the invalid pet ID
        Order createdOrder = response.as(Order.class);
        Assert.assertEquals(createdOrder.getPetId(), 999999L);
        Assert.assertNotNull(createdOrder.getId());
    }
    
    @Test(description = "Get order by valid ID")
    public void testGetOrderById_Success() {
        // Create an order
        Order order = createSampleOrder();
        Order createdOrder = storeApiService.createOrder(order);
        
        // Retrieve the order
        Order retrievedOrder = storeApiService.getOrderById(createdOrder.getId());
        
        Assert.assertNotNull(retrievedOrder);
        Assert.assertEquals(retrievedOrder.getId(), createdOrder.getId());
        Assert.assertEquals(retrievedOrder.getPetId(), createdOrder.getPetId());
    }
    
    @Test(description = "Get order by non-existent ID")
    public void testGetOrderById_NotFound() {
        Response response = storeApiService.getOrderByIdResponse(999999L);
        
        Assert.assertEquals(response.getStatusCode(), 404);
    }
    
    @Test(description = "Delete order by valid ID")
    public void testDeleteOrder_Success() {
        // Create an order
        Order order = createSampleOrder();
        Order createdOrder = storeApiService.createOrder(order);
        
        // Delete the order
        Response response = storeApiService.deleteOrder(createdOrder.getId());
        
        Assert.assertEquals(response.getStatusCode(), 200);
        
        // Verify order is deleted
        Response getResponse = storeApiService.getOrderByIdResponse(createdOrder.getId());
        Assert.assertEquals(getResponse.getStatusCode(), 404);
    }
    
    @Test(description = "Delete order by non-existent ID")
    public void testDeleteOrder_NotFound() {
        Response response = storeApiService.deleteOrder(999999L);
        
        Assert.assertEquals(response.getStatusCode(), 404);
    }
    
    // ==================== USER API TESTS ====================
    
    @Test(description = "Create a new user")
    public void testCreateUser_Success() {
        User user = createSampleUser();
        
        Response response = userApiService.createUser(user);
        
        Assert.assertEquals(response.getStatusCode(), 200);
    }
    
    @Test(description = "Create multiple users")
    public void testCreateUsersWithArray() {
        User[] users = {
            createSampleUser("user1", "user1@example.com"),
            createSampleUser("user2", "user2@example.com"),
            createSampleUser("user3", "user3@example.com")
        };
        
        Response response = userApiService.createUsersWithArray(users);
        
        Assert.assertEquals(response.getStatusCode(), 200);
    }
    
    @Test(description = "Create multiple users with list")
    public void testCreateUsersWithList() {
        List<User> users = Arrays.asList(
            createSampleUser("listuser1", "listuser1@example.com"),
            createSampleUser("listuser2", "listuser2@example.com")
        );
        
        Response response = userApiService.createUsersWithList(users);
        
        Assert.assertEquals(response.getStatusCode(), 200);
    }
    
    @Test(description = "Get user by valid username")
    public void testGetUserByUsername_Success() {
        // First create a user
        User user = createSampleUser("testuser", "testuser@example.com");
        userApiService.createUser(user);
        
        // Then retrieve it
        User retrievedUser = userApiService.getUserByUsername("testuser");
        
        Assert.assertNotNull(retrievedUser);
        Assert.assertEquals(retrievedUser.getUsername(), "testuser");
        Assert.assertEquals(retrievedUser.getEmail(), "testuser@example.com");
    }
    
    @Test(description = "Get user by non-existent username")
    public void testGetUserByUsername_NotFound() {
        Response response = userApiService.getUserByUsernameResponse("nonexistentuser");
        
        Assert.assertEquals(response.getStatusCode(), 404);
    }
    
    @Test(description = "Update user by username")
    public void testUpdateUser_Success() {
        // Create a user
        User user = createSampleUser("updateuser", "updateuser@example.com");
        userApiService.createUser(user);
        
        // Update the user
        user.setEmail("updated@example.com");
        user.setPhone("123-456-7890");
        
        Response response = userApiService.updateUser("updateuser", user);
        
        Assert.assertEquals(response.getStatusCode(), 200);
        
        // Verify the update
        User updatedUser = userApiService.getUserByUsername("updateuser");
        Assert.assertEquals(updatedUser.getEmail(), "updated@example.com");
        Assert.assertEquals(updatedUser.getPhone(), "123-456-7890");
    }
    
    @Test(description = "Delete user by username")
    public void testDeleteUser_Success() {
        // Create a user
        User user = createSampleUser("deleteuser", "deleteuser@example.com");
        userApiService.createUser(user);
        
        // Delete the user
        Response response = userApiService.deleteUser("deleteuser");
        
        Assert.assertEquals(response.getStatusCode(), 200);
        
        // Verify user is deleted
        Response getResponse = userApiService.getUserByUsernameResponse("deleteuser");
        Assert.assertEquals(getResponse.getStatusCode(), 404);
    }
    
    @Test(description = "User login")
    public void testUserLogin() {
        // Create a user first
        User user = createSampleUser("loginuser", "loginuser@example.com");
        userApiService.createUser(user);
        
        // Login
        Response response = userApiService.login("loginuser", "password123");
        
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertNotNull(response.getHeader("X-Rate-Limit"));
        Assert.assertNotNull(response.getHeader("X-Expires-After"));
    }
    
    @Test(description = "User login with invalid credentials")
    public void testUserLogin_InvalidCredentials() {
        Response response = userApiService.login("invaliduser", "wrongpassword");
        
        Assert.assertEquals(response.getStatusCode(), 400);
    }
    
    @Test(description = "User logout")
    public void testUserLogout() {
        Response response = userApiService.logout();
        
        Assert.assertEquals(response.getStatusCode(), 200);
    }
    
    // ==================== DATA PROVIDERS ====================
    
    @DataProvider(name = "petStatusData")
    public Object[][] petStatusData() {
        return new Object[][] {
            {"available"},
            {"pending"},
            {"sold"}
        };
    }
    
    @Test(dataProvider = "petStatusData", description = "Test pet status validation")
    public void testPetStatusValidation(String status) {
        Pet pet = createSamplePet();
        pet.setStatus(status);
        
        Pet createdPet = petApiService.createPet(pet);
        
        Assert.assertEquals(createdPet.getStatus(), status);
    }
    
    @DataProvider(name = "orderStatusData")
    public Object[][] orderStatusData() {
        return new Object[][] {
            {"placed"},
            {"approved"},
            {"delivered"}
        };
    }
    
    @Test(dataProvider = "orderStatusData", description = "Test order status validation")
    public void testOrderStatusValidation(String status) {
        Order order = createSampleOrder();
        order.setStatus(status);
        
        Order createdOrder = storeApiService.createOrder(order);
        
        Assert.assertEquals(createdOrder.getStatus(), status);
    }
    
    // ==================== HELPER METHODS ====================
    
    private Pet createSamplePet() {
        Pet pet = new Pet();
        pet.setName("Buddy");
        pet.setStatus("available");
        
        Category category = new Category();
        category.setId(1L);
        category.setName("Dogs");
        pet.setCategory(category);
        
        pet.setPhotoUrls(Arrays.asList("http://example.com/photo1.jpg"));
        
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("friendly");
        pet.setTags(Arrays.asList(tag));
        
        return pet;
    }
    
    private Pet createCompletePet() {
        Pet pet = new Pet();
        pet.setName("Complete Pet");
        pet.setStatus("pending");
        
        Category category = new Category();
        category.setId(2L);
        category.setName("Cats");
        pet.setCategory(category);
        
        pet.setPhotoUrls(Arrays.asList(
            "http://example.com/photo1.jpg",
            "http://example.com/photo2.jpg"
        ));
        
        Tag tag1 = new Tag();
        tag1.setId(1L);
        tag1.setName("friendly");
        
        Tag tag2 = new Tag();
        tag2.setId(2L);
        tag2.setName("playful");
        
        pet.setTags(Arrays.asList(tag1, tag2));
        
        return pet;
    }
    
    private Order createSampleOrder() {
        Order order = new Order();
        order.setPetId(1L);
        order.setQuantity(1);
        order.setShipDate("2024-01-01T10:00:00.000Z");
        order.setStatus("placed");
        order.setComplete(true);
        
        return order;
    }
    
    private User createSampleUser() {
        return createSampleUser("testuser", "testuser@example.com");
    }
    
    private User createSampleUser(String username, String email) {
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail(email);
        user.setPassword("password123");
        user.setPhone("123-456-7890");
        user.setUserStatus(1);
        
        return user;
    }
} 