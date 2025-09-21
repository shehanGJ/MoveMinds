package com.java.moveminds.dto.requests;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
public class PayHerePaymentRequest {
    
    @NotNull(message = "Program ID is required")
    private Integer programId;
    
    @NotBlank(message = "Customer name is required")
    private String customerName;
    
    @NotBlank(message = "Customer email is required")
    private String customerEmail;
    
    @NotBlank(message = "Customer phone is required")
    private String customerPhone;
    
    @NotBlank(message = "Customer address is required")
    private String customerAddress;
    
    @NotBlank(message = "Customer city is required")
    private String customerCity;
    
    @NotBlank(message = "Customer country is required")
    private String customerCountry;
    
    @Positive(message = "Amount must be positive")
    private Double amount;
    
    @NotBlank(message = "Currency is required")
    private String currency = "LKR";
    
    @NotBlank(message = "Order ID is required")
    private String orderId;
    
    @NotBlank(message = "Item name is required")
    private String itemName;
    
    private String itemDescription;
}
