package com.java.moveminds.dto.response;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class PayHerePaymentResponse {
    
    private String paymentUrl;
    private String orderId;
    private String merchantId;
    private String merchantSecret;
    private String amount;
    private String currency;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String customerAddress;
    private String customerCity;
    private String customerCountry;
    private String itemName;
    private String itemDescription;
    private String returnUrl;
    private String cancelUrl;
    private String notifyUrl;
    private String hash;
    private String status;
    private String message;
}
