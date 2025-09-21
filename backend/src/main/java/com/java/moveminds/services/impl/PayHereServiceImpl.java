package com.java.moveminds.services.impl;

import com.java.moveminds.config.PayHereConfig;
import com.java.moveminds.dto.requests.PayHerePaymentRequest;
import com.java.moveminds.dto.requests.PayHereNotifyRequest;
import com.java.moveminds.dto.response.PayHerePaymentResponse;
import com.java.moveminds.services.PayHereService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayHereServiceImpl implements PayHereService {
    
    private final PayHereConfig payHereConfig;
    
    @Override
    public PayHerePaymentResponse createPaymentRequest(PayHerePaymentRequest request) {
        try {
            log.info("Creating PayHere payment request for amount: {}", request.getAmount());
            log.info("PayHere config - Merchant ID: {}, Sandbox Mode: {}", 
                    payHereConfig.getMerchantId(), payHereConfig.isSandboxMode());
            log.info("PayHere URLs - Return: {}, Cancel: {}, Notify: {}", 
                    payHereConfig.getReturnUrl(), payHereConfig.getCancelUrl(), payHereConfig.getNotifyUrl());
            
            String orderId = generateOrderId();
            String merchantId = "1232127";
            String merchantSecret = "123456789";
            String hash = generateHash(
                merchantId,
                orderId,
                String.valueOf(request.getAmount()),
                request.getCurrency(),
                merchantSecret
            );
            
            // Use hardcoded values to ensure PayHere works immediately
            String paymentUrl = "https://sandbox.payhere.lk/pay/checkout";
            String returnUrl = "http://localhost:5173/payment/success";
            String cancelUrl = "http://localhost:5173/payment/cancel";
            String notifyUrl = "http://localhost:8081/api/payment/payhere/notify";
            
            return PayHerePaymentResponse.builder()
                .paymentUrl(paymentUrl)
                .orderId(orderId)
                .merchantId(merchantId)
                .merchantSecret(merchantSecret)
                .amount(String.valueOf(request.getAmount()))
                .currency(request.getCurrency())
                .customerName(request.getCustomerName())
                .customerEmail(request.getCustomerEmail())
                .customerPhone(request.getCustomerPhone())
                .customerAddress(request.getCustomerAddress())
                .customerCity(request.getCustomerCity())
                .customerCountry(request.getCustomerCountry())
                .itemName(request.getItemName())
                .itemDescription(request.getItemDescription() != null ? request.getItemDescription() : "Fitness Program")
                .returnUrl(returnUrl)
                .cancelUrl(cancelUrl)
                .notifyUrl(notifyUrl)
                .hash(hash)
                .status("success")
                .message("Payment request created successfully")
                .build();
                
        } catch (Exception e) {
            log.error("Error creating PayHere payment request", e);
            return PayHerePaymentResponse.builder()
                .status("error")
                .message("Failed to create payment request: " + e.getMessage())
                .build();
        }
    }
    
    @Override
    public boolean verifyPaymentNotification(PayHereNotifyRequest request) {
        try {
            String expectedHash = generateHash(
                request.getMerchant_id(),
                request.getOrder_id(),
                request.getPayhere_amount(),
                request.getPayhere_currency(),
                payHereConfig.getMerchantSecret()
            );
            
            return expectedHash.equals(request.getMd5sig());
        } catch (Exception e) {
            log.error("Error verifying PayHere payment notification", e);
            return false;
        }
    }
    
    @Override
    public String generateHash(String merchantId, String orderId, String amount, String currency, String merchantSecret) {
        try {
            String hashString = merchantId + orderId + amount + currency + merchantSecret;
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(hashString.getBytes());
            
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            log.error("Error generating MD5 hash", e);
            throw new RuntimeException("Error generating hash", e);
        }
    }
    
    private String generateOrderId() {
        return "MM_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
}
