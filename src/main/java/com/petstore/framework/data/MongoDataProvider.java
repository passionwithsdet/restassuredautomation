package com.petstore.framework.data;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.petstore.framework.config.TestConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.testng.annotations.DataProvider;

import java.util.*;

/**
 * MongoDB Data Provider for data-driven testing
 * Standalone class for MongoDB data sources
 */
public class MongoDataProvider {
    private static final Logger logger = LogManager.getLogger(MongoDataProvider.class);
    private final TestConfig config;
    private MongoClient mongoClient;
    private MongoDatabase database;
    
    public MongoDataProvider() {
        this.config = TestConfig.getInstance();
        initializeMongoConnection();
    }
    
    private void initializeMongoConnection() {
        try {
            mongoClient = MongoClients.create(config.getMongoDbUri());
            database = mongoClient.getDatabase(config.getMongoDbDatabase());
            logger.info("MongoDB connection established: {}", config.getMongoDbUri());
        } catch (Exception e) {
            logger.error("Failed to connect to MongoDB: {}", config.getMongoDbUri(), e);
        }
    }
    
    /**
     * MongoDB Data Provider for test data
     */
    @org.testng.annotations.DataProvider(name = "mongoData")
    public Object[][] getMongoData(String collectionName, Bson filter) {
        List<Map<String, Object>> data = new ArrayList<>();
        
        try {
            MongoCollection<Document> collection = database.getCollection(collectionName);
            FindIterable<Document> documents;
            
            if (filter != null) {
                documents = collection.find(filter);
            } else {
                documents = collection.find();
            }
            
            for (Document doc : documents) {
                Map<String, Object> row = new HashMap<>();
                for (String key : doc.keySet()) {
                    row.put(key, doc.get(key));
                }
                data.add(row);
            }
            
            logger.info("Retrieved {} documents from collection: {}", data.size(), collectionName);
            
        } catch (Exception e) {
            logger.error("Error retrieving data from MongoDB collection: {}", collectionName, e);
        }
        
        return convertToObjectArray(data);
    }
    
    /**
     * Convert list to object array for TestNG data provider
     */
    private Object[][] convertToObjectArray(List<?> data) {
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
     * Get pet test data from MongoDB
     */
    @org.testng.annotations.DataProvider(name = "mongoPetData")
    public Object[][] getMongoPetData() {
        String collectionName = config.getMongoDbCollection("pets");
        return getMongoData(collectionName, null);
    }
    
    /**
     * Get pet test data by status from MongoDB
     */
    @org.testng.annotations.DataProvider(name = "mongoPetDataByStatus")
    public Object[][] getMongoPetDataByStatus(String status) {
        String collectionName = config.getMongoDbCollection("pets");
        Bson filter = Filters.eq("status", status);
        return getMongoData(collectionName, filter);
    }
    
    /**
     * Get user test data from MongoDB
     */
    @org.testng.annotations.DataProvider(name = "mongoUserData")
    public Object[][] getMongoUserData() {
        String collectionName = config.getMongoDbCollection("users");
        return getMongoData(collectionName, null);
    }
    
    /**
     * Get order test data from MongoDB
     */
    @org.testng.annotations.DataProvider(name = "mongoOrderData")
    public Object[][] getMongoOrderData() {
        String collectionName = config.getMongoDbCollection("orders");
        return getMongoData(collectionName, null);
    }
    
    /**
     * Insert test data into MongoDB
     */
    public void insertTestData(String collectionName, List<Document> documents) {
        try {
            MongoCollection<Document> collection = database.getCollection(collectionName);
            collection.insertMany(documents);
            logger.info("Inserted {} documents into collection: {}", documents.size(), collectionName);
        } catch (Exception e) {
            logger.error("Error inserting data into MongoDB collection: {}", collectionName, e);
        }
    }
    
    /**
     * Insert single document into MongoDB
     */
    public void insertTestData(String collectionName, Document document) {
        try {
            MongoCollection<Document> collection = database.getCollection(collectionName);
            collection.insertOne(document);
            logger.info("Inserted document into collection: {}", collectionName);
        } catch (Exception e) {
            logger.error("Error inserting document into MongoDB collection: {}", collectionName, e);
        }
    }
    
    /**
     * Update document in MongoDB
     */
    public void updateTestData(String collectionName, Bson filter, Document update) {
        try {
            MongoCollection<Document> collection = database.getCollection(collectionName);
            collection.updateOne(filter, new Document("$set", update));
            logger.info("Updated document in collection: {}", collectionName);
        } catch (Exception e) {
            logger.error("Error updating document in MongoDB collection: {}", collectionName, e);
        }
    }
    
    /**
     * Delete documents from MongoDB
     */
    public void deleteTestData(String collectionName, Bson filter) {
        try {
            MongoCollection<Document> collection = database.getCollection(collectionName);
            collection.deleteMany(filter);
            logger.info("Deleted documents from collection: {}", collectionName);
        } catch (Exception e) {
            logger.error("Error deleting documents from MongoDB collection: {}", collectionName, e);
        }
    }
    
    /**
     * Clear all test data from collection
     */
    public void clearTestData(String collectionName) {
        try {
            MongoCollection<Document> collection = database.getCollection(collectionName);
            collection.deleteMany(new Document());
            logger.info("Cleared all data from collection: {}", collectionName);
        } catch (Exception e) {
            logger.error("Error clearing data from MongoDB collection: {}", collectionName, e);
        }
    }
    
    /**
     * Get document count from collection
     */
    public long getDocumentCount(String collectionName, Bson filter) {
        try {
            MongoCollection<Document> collection = database.getCollection(collectionName);
            if (filter != null) {
                return collection.countDocuments(filter);
            } else {
                return collection.countDocuments();
            }
        } catch (Exception e) {
            logger.error("Error counting documents in MongoDB collection: {}", collectionName, e);
            return 0;
        }
    }
    
    /**
     * Create test data for pets
     */
    public void createPetTestData() {
        String collectionName = config.getMongoDbCollection("pets");
        List<Document> pets = Arrays.asList(
            new Document()
                .append("name", "Fluffy")
                .append("status", "available")
                .append("category", "Cats")
                .append("description", "Friendly cat with long fur"),
            new Document()
                .append("name", "Buddy")
                .append("status", "available")
                .append("category", "Dogs")
                .append("description", "Loyal golden retriever"),
            new Document()
                .append("name", "Max")
                .append("status", "pending")
                .append("category", "Dogs")
                .append("description", "Energetic border collie"),
            new Document()
                .append("name", "Luna")
                .append("status", "sold")
                .append("category", "Cats")
                .append("description", "Graceful siamese cat")
        );
        
        insertTestData(collectionName, pets);
    }
    
    /**
     * Create test data for users
     */
    public void createUserTestData() {
        String collectionName = config.getMongoDbCollection("users");
        List<Document> users = Arrays.asList(
            new Document()
                .append("username", "testuser1")
                .append("email", "user1@example.com")
                .append("firstName", "John")
                .append("lastName", "Doe")
                .append("phone", "+1234567890"),
            new Document()
                .append("username", "testuser2")
                .append("email", "user2@example.com")
                .append("firstName", "Jane")
                .append("lastName", "Smith")
                .append("phone", "+0987654321")
        );
        
        insertTestData(collectionName, users);
    }
    
    /**
     * Create test data for orders
     */
    public void createOrderTestData() {
        String collectionName = config.getMongoDbCollection("orders");
        List<Document> orders = Arrays.asList(
            new Document()
                .append("orderId", 1)
                .append("petId", 1)
                .append("quantity", 1)
                .append("shipDate", new Date())
                .append("status", "placed"),
            new Document()
                .append("orderId", 2)
                .append("petId", 2)
                .append("quantity", 2)
                .append("shipDate", new Date())
                .append("status", "delivered")
        );
        
        insertTestData(collectionName, orders);
    }
    
    /**
     * Initialize all test data
     */
    public void initializeTestData() {
        logger.info("Initializing MongoDB test data...");
        createPetTestData();
        createUserTestData();
        createOrderTestData();
        logger.info("MongoDB test data initialization completed");
    }
    
    /**
     * Clean up all test data
     */
    public void cleanupTestData() {
        logger.info("Cleaning up MongoDB test data...");
        clearTestData(config.getMongoDbCollection("pets"));
        clearTestData(config.getMongoDbCollection("users"));
        clearTestData(config.getMongoDbCollection("orders"));
        logger.info("MongoDB test data cleanup completed");
    }
    
    /**
     * Close MongoDB connection
     */
    public void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
            logger.info("MongoDB connection closed");
        }
    }
    
    /**
     * Get MongoDB client instance
     */
    public MongoClient getMongoClient() {
        return mongoClient;
    }
    
    /**
     * Get MongoDB database instance
     */
    public MongoDatabase getDatabase() {
        return database;
    }
} 