package com.java.moveminds.services;

import com.java.moveminds.dto.requests.PayHerePaymentRequest;
import com.java.moveminds.dto.requests.PayHereNotifyRequest;
import com.java.moveminds.dto.response.PayHerePaymentResponse;

public interface PayHereService {
    
    PayHerePaymentResponse createPaymentRequest(PayHerePaymentRequest request);
    
    boolean verifyPaymentNotification(PayHereNotifyRequest request);
    
    String generateHash(String merchantId, String orderId, String amount, String currency, String merchantSecret);
}
