// MongoDB initialization script for PetStore API Test Framework
// This script creates the test database and collections with sample data

// Switch to the test database
db = db.getSiblingDB('petstore_test');

// Create collections
db.createCollection('pets');
db.createCollection('users');
db.createCollection('orders');

// Insert sample pet data
db.pets.insertMany([
    {
        name: "Fluffy",
        status: "available",
        category: "Cats",
        description: "Friendly cat with long fur",
        photoUrls: ["http://example.com/fluffy1.jpg", "http://example.com/fluffy2.jpg"],
        tags: [
            { id: 1, name: "friendly" },
            { id: 2, name: "playful" }
        ]
    },
    {
        name: "Buddy",
        status: "available",
        category: "Dogs",
        description: "Loyal golden retriever",
        photoUrls: ["http://example.com/buddy1.jpg"],
        tags: [
            { id: 3, name: "loyal" },
            { id: 4, name: "trained" }
        ]
    },
    {
        name: "Max",
        status: "pending",
        category: "Dogs",
        description: "Energetic border collie",
        photoUrls: ["http://example.com/max1.jpg"],
        tags: [
            { id: 5, name: "energetic" },
            { id: 6, name: "smart" }
        ]
    },
    {
        name: "Luna",
        status: "sold",
        category: "Cats",
        description: "Graceful siamese cat",
        photoUrls: ["http://example.com/luna1.jpg"],
        tags: [
            { id: 7, name: "graceful" },
            { id: 8, name: "elegant" }
        ]
    },
    {
        name: "Rex",
        status: "available",
        category: "Dogs",
        description: "Strong german shepherd",
        photoUrls: ["http://example.com/rex1.jpg"],
        tags: [
            { id: 9, name: "strong" },
            { id: 10, name: "protective" }
        ]
    }
]);

// Insert sample user data
db.users.insertMany([
    {
        username: "testuser1",
        email: "user1@example.com",
        firstName: "John",
        lastName: "Doe",
        phone: "+1234567890",
        userStatus: 1
    },
    {
        username: "testuser2",
        email: "user2@example.com",
        firstName: "Jane",
        lastName: "Smith",
        phone: "+0987654321",
        userStatus: 1
    },
    {
        username: "testuser3",
        email: "user3@example.com",
        firstName: "Bob",
        lastName: "Johnson",
        phone: "+1122334455",
        userStatus: 0
    }
]);

// Insert sample order data
db.orders.insertMany([
    {
        orderId: 1,
        petId: 1,
        quantity: 1,
        shipDate: new Date(),
        status: "placed",
        complete: false
    },
    {
        orderId: 2,
        petId: 2,
        quantity: 2,
        shipDate: new Date(),
        status: "delivered",
        complete: true
    },
    {
        orderId: 3,
        petId: 3,
        quantity: 1,
        shipDate: new Date(),
        status: "pending",
        complete: false
    }
]);

// Create indexes for better performance
db.pets.createIndex({ "name": 1 });
db.pets.createIndex({ "status": 1 });
db.pets.createIndex({ "category": 1 });

db.users.createIndex({ "username": 1 }, { unique: true });
db.users.createIndex({ "email": 1 });

db.orders.createIndex({ "orderId": 1 }, { unique: true });
db.orders.createIndex({ "petId": 1 });
db.orders.createIndex({ "status": 1 });

// Print summary
print("MongoDB initialization completed successfully!");
print("Database: " + db.getName());
print("Collections created: " + db.getCollectionNames().join(", "));
print("Pets count: " + db.pets.countDocuments());
print("Users count: " + db.users.countDocuments());
print("Orders count: " + db.orders.countDocuments()); 