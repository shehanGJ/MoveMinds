package com.java.moveminds.controllers;

import com.java.moveminds.dto.requests.PayHerePaymentRequest;
import com.java.moveminds.dto.requests.PayHereNotifyRequest;
import com.java.moveminds.dto.response.PayHerePaymentResponse;
import com.java.moveminds.entities.FitnessProgramEntity;
import com.java.moveminds.entities.UserEntity;
import com.java.moveminds.entities.UserProgramEntity;
import com.java.moveminds.enums.Status;
import com.java.moveminds.repositories.FitnessProgramEntityRepository;
import com.java.moveminds.repositories.UserEntityRepository;
import com.java.moveminds.repositories.UserProgramEntityRepository;
import com.java.moveminds.services.PayHereService;
import com.java.moveminds.config.PayHereConfig;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.Date;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    
    private final PayHereService payHereService;
    private final FitnessProgramEntityRepository fitnessProgramRepository;
    private final UserEntityRepository userRepository;
    private final UserProgramEntityRepository userProgramRepository;
    private final PayHereConfig payHereConfig;
    
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("PaymentController is working!");
    }
    
    @GetMapping("/config")
    public ResponseEntity<String> testConfig() {
        return ResponseEntity.ok("PayHere Config - Merchant ID: " + payHereConfig.getMerchantId() + 
                                ", Return URL: " + payHereConfig.getReturnUrl() + 
                                ", Sandbox Mode: " + payHereConfig.isSandboxMode());
    }
    
    @PostMapping("/payhere/create")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<PayHerePaymentResponse> createPayment(
            @Valid @RequestBody PayHerePaymentRequest request,
            Principal principal) {
        
        log.info("Received payment request for program ID: {}", request.getProgramId());
        log.info("User: {}", principal.getName());
        
        try {
            // Get user and program details
            UserEntity user = userRepository.findByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            FitnessProgramEntity program = fitnessProgramRepository.findById(request.getProgramId())
                    .orElseThrow(() -> new RuntimeException("Program not found"));
            
            // Check if user is already enrolled
            if (userProgramRepository.existsByUserByUserIdAndFitnessProgramByProgramId(user, program)) {
                return ResponseEntity.badRequest()
                        .body(PayHerePaymentResponse.builder()
                                .status("error")
                                .message("You are already enrolled in this program")
                                .build());
            }
            
            // Set user details from authenticated user
            request.setCustomerName(user.getFirstName() + " " + user.getLastName());
            request.setCustomerEmail(user.getEmail());
            request.setCustomerPhone("0000000000"); // Default phone since not in UserEntity
            request.setCustomerAddress("Default Address"); // Default address since not in UserEntity
            request.setCustomerCity(user.getCity() != null ? user.getCity().getName() : "Default City");
            request.setCustomerCountry("Sri Lanka");
            request.setAmount(program.getPrice().doubleValue());
            request.setItemName(program.getName());
            request.setItemDescription(program.getDescription());
            
            PayHerePaymentResponse response = payHereService.createPaymentRequest(request);
            
            if ("success".equals(response.getStatus())) {
                log.info("Payment request created for user: {} and program: {}", user.getUsername(), program.getName());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            log.error("Error creating payment request", e);
            return ResponseEntity.badRequest()
                    .body(PayHerePaymentResponse.builder()
                            .status("error")
                            .message("Failed to create payment request: " + e.getMessage())
                            .build());
        }
    }
    
    @PostMapping("/payhere/notify")
    public ResponseEntity<String> handlePaymentNotification(@RequestBody PayHereNotifyRequest request) {
        try {
            log.info("Received PayHere notification: {}", request);
            
            // Verify the payment notification
            if (!payHereService.verifyPaymentNotification(request)) {
                log.warn("Invalid payment notification received");
                return ResponseEntity.badRequest().body("Invalid notification");
            }
            
            // Process successful payment
            if ("2".equals(request.getStatus_code())) { // Payment successful
                processSuccessfulPayment(request);
                log.info("Payment processed successfully for order: {}", request.getOrder_id());
            } else {
                log.info("Payment failed or cancelled for order: {}", request.getOrder_id());
            }
            
            return ResponseEntity.ok("OK");
            
        } catch (Exception e) {
            log.error("Error processing payment notification", e);
            return ResponseEntity.badRequest().body("Error processing notification");
        }
    }
    
    private void processSuccessfulPayment(PayHereNotifyRequest request) {
        try {
            // Extract program ID from order ID (assuming format: MM_timestamp_programId)
            String orderId = request.getOrder_id();
            String[] parts = orderId.split("_");
            if (parts.length >= 3) {
                // For now, we'll need to store the mapping between order ID and program ID
                // This is a simplified approach - in production, you'd want to store this mapping
                log.info("Processing payment for order: {}", orderId);
                
                // You might want to implement a more robust way to track order-to-program mapping
                // For now, we'll log the successful payment
                log.info("Payment successful - Order ID: {}, Amount: {}, Status: {}", 
                        orderId, request.getPayhere_amount(), request.getStatus_message());
            }
        } catch (Exception e) {
            log.error("Error processing successful payment", e);
        }
    }
    
    @PostMapping("/payhere/complete")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<String> completePayment(
            @RequestParam String orderId,
            @RequestParam String programId,
            Principal principal) {
        
        try {
            UserEntity user = userRepository.findByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            FitnessProgramEntity program = fitnessProgramRepository.findById(Integer.parseInt(programId))
                    .orElseThrow(() -> new RuntimeException("Program not found"));
            
            // Check if already enrolled
            if (userProgramRepository.existsByUserByUserIdAndFitnessProgramByProgramId(user, program)) {
                return ResponseEntity.ok("Already enrolled");
            }
            
            // Create user program enrollment
            UserProgramEntity userProgram = new UserProgramEntity();
            userProgram.setUserByUserId(user);
            userProgram.setFitnessProgramByProgramId(program);
            userProgram.setStartDate(new Date(System.currentTimeMillis()));
            userProgram.setEndDate(Date.valueOf(LocalDate.now().plusDays(30))); // 30 days default
            userProgram.setStatus(Status.ACTIVE);
            
            userProgramRepository.save(userProgram);
            
            log.info("User {} successfully enrolled in program {} after payment", user.getUsername(), program.getName());
            
            return ResponseEntity.ok("Payment completed and enrollment successful");
            
        } catch (Exception e) {
            log.error("Error completing payment", e);
            return ResponseEntity.badRequest().body("Error completing payment: " + e.getMessage());
        }
    }
}
