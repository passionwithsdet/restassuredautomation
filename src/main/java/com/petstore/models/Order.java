package com.petstore.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Order model for PetStore API
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("petId")
    private Long petId;
    
    @JsonProperty("quantity")
    private Integer quantity;
    
    @JsonProperty("shipDate")
    private String shipDate;
    
    @JsonProperty("status")
    private String status; // placed, approved, delivered
    
    @JsonProperty("complete")
    private Boolean complete;
    
    public Order() {}
    
    public Order(Long petId, Integer quantity, String status) {
        this.petId = petId;
        this.quantity = quantity;
        this.status = status;
        this.complete = false;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getPetId() {
        return petId;
    }
    
    public void setPetId(Long petId) {
        this.petId = petId;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public String getShipDate() {
        return shipDate;
    }
    
    public void setShipDate(String shipDate) {
        this.shipDate = shipDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Boolean getComplete() {
        return complete;
    }
    
    public void setComplete(Boolean complete) {
        this.complete = complete;
    }
    
    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", petId=" + petId +
                ", quantity=" + quantity +
                ", shipDate='" + shipDate + '\'' +
                ", status='" + status + '\'' +
                ", complete=" + complete +
                '}';
    }
} 