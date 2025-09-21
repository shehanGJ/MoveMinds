package com.java.moveminds.controllers.admin;

import com.java.moveminds.dto.response.admin.*;
import com.java.moveminds.services.admin.AdminAnalyticsService;
import com.java.moveminds.repositories.UserProgramEntityRepository;
import com.java.moveminds.entities.UserProgramEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for admin analytics operations.
 */
@Slf4j
@RestController
@RequestMapping("/admin/analytics")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminAnalyticsController {
    
    private final AdminAnalyticsService adminAnalyticsService;
    private final UserProgramEntityRepository userProgramRepository;
    
    /**
     * Get comprehensive analytics overview
     */
    @GetMapping("/overview")
    public ResponseEntity<AdminAnalyticsResponse> getAnalyticsOverview(Principal principal) {
        log.info("Admin {} requesting analytics overview", principal.getName());
        
        AdminAnalyticsResponse analytics = adminAnalyticsService.getAnalyticsOverview(principal);
        return ResponseEntity.ok(analytics);
    }
    
    /**
     * Get user growth analytics for a specific period
     */
    @GetMapping("/user-growth")
    public ResponseEntity<UserGrowthAnalyticsResponse> getUserGrowthAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Principal principal) {
        
        log.info("Admin {} requesting user growth analytics from {} to {}", 
                principal.getName(), startDate, endDate);
        
        UserGrowthAnalyticsResponse analytics = adminAnalyticsService.getUserGrowthAnalytics(
                principal, startDate, endDate);
        return ResponseEntity.ok(analytics);
    }
    
    /**
     * Get program analytics
     */
    @GetMapping("/programs")
    public ResponseEntity<ProgramAnalyticsResponse> getProgramAnalytics(Principal principal) {
        log.info("Admin {} requesting program analytics", principal.getName());
        
        ProgramAnalyticsResponse analytics = adminAnalyticsService.getProgramAnalytics(principal);
        return ResponseEntity.ok(analytics);
    }
    
    /**
     * Get revenue analytics for a specific period
     */
    @GetMapping("/revenue")
    public ResponseEntity<RevenueAnalyticsResponse> getRevenueAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Principal principal) {
        
        log.info("Admin {} requesting revenue analytics from {} to {}", 
                principal.getName(), startDate, endDate);
        
        RevenueAnalyticsResponse analytics = adminAnalyticsService.getRevenueAnalytics(
                principal, startDate, endDate);
        return ResponseEntity.ok(analytics);
    }
    
    /**
     * Get real-time metrics
     */
    @GetMapping("/real-time")
    public ResponseEntity<Object> getRealTimeMetrics(Principal principal) {
        log.info("Admin {} requesting real-time metrics", principal.getName());
        
        Object metrics = adminAnalyticsService.getRealTimeMetrics(principal);
        return ResponseEntity.ok(metrics);
    }
    
    /**
     * Get analytics dashboard data (combined endpoint for frontend)
     */
    @GetMapping("/dashboard")
    public ResponseEntity<AdminAnalyticsResponse> getDashboardAnalytics(Principal principal) {
        log.info("Admin {} requesting dashboard analytics", principal.getName());
        
        AdminAnalyticsResponse analytics = adminAnalyticsService.getAnalyticsOverview(principal);
        return ResponseEntity.ok(analytics);
    }
    
    /**
     * Debug endpoint to help troubleshoot revenue calculation issues
     */
    @GetMapping("/debug/revenue")
    public ResponseEntity<Map<String, Object>> debugRevenue(Principal principal) {
        log.info("Admin {} requesting revenue debug information", principal.getName());
        
        Map<String, Object> debugInfo = new HashMap<>();
        
        try {
            // Get all enrollments
            List<UserProgramEntity> enrollments = userProgramRepository.findAll();
            debugInfo.put("totalEnrollments", enrollments.size());
            
            // Calculate total revenue manually
            BigDecimal totalRevenue = BigDecimal.ZERO;
            int enrollmentsWithPrice = 0;
            
            for (UserProgramEntity enrollment : enrollments) {
                BigDecimal programPrice = enrollment.getFitnessProgramByProgramId().getPrice();
                if (programPrice != null) {
                    totalRevenue = totalRevenue.add(programPrice);
                    enrollmentsWithPrice++;
                }
            }
            
            debugInfo.put("totalRevenue", totalRevenue);
            debugInfo.put("enrollmentsWithPrice", enrollmentsWithPrice);
            
            // Check monthly revenue
            LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
            
            BigDecimal monthlyRevenue = BigDecimal.ZERO;
            int monthlyEnrollments = 0;
            
            for (UserProgramEntity enrollment : enrollments) {
                // Check if enrollment started this month
                LocalDate enrollmentDate = enrollment.getStartDate().toLocalDate();
                if (enrollmentDate.isAfter(startOfMonth.minusDays(1))) {
                    BigDecimal programPrice = enrollment.getFitnessProgramByProgramId().getPrice();
                    if (programPrice != null) {
                        monthlyRevenue = monthlyRevenue.add(programPrice);
                        monthlyEnrollments++;
                    }
                }
            }
            
            debugInfo.put("monthlyRevenue", monthlyRevenue);
            debugInfo.put("monthlyEnrollments", monthlyEnrollments);
            debugInfo.put("startOfMonth", startOfMonth.toString());
            
            // Check if created_at field exists and has data
            boolean hasCreatedAtField = false;
            int enrollmentsWithCreatedAt = 0;
            
            try {
                for (UserProgramEntity enrollment : enrollments) {
                    if (enrollment.getCreatedAt() != null) {
                        hasCreatedAtField = true;
                        enrollmentsWithCreatedAt++;
                    }
                }
            } catch (Exception e) {
                debugInfo.put("createdAtFieldError", e.getMessage());
            }
            
            debugInfo.put("hasCreatedAtField", hasCreatedAtField);
            debugInfo.put("enrollmentsWithCreatedAt", enrollmentsWithCreatedAt);
            
            // Sample enrollment data
            if (!enrollments.isEmpty()) {
                UserProgramEntity sample = enrollments.get(0);
                Map<String, Object> sampleData = new HashMap<>();
                sampleData.put("id", sample.getId());
                sampleData.put("startDate", sample.getStartDate());
                sampleData.put("createdAt", sample.getCreatedAt());
                sampleData.put("programPrice", sample.getFitnessProgramByProgramId().getPrice());
                sampleData.put("programName", sample.getFitnessProgramByProgramId().getName());
                debugInfo.put("sampleEnrollment", sampleData);
            }
            
        } catch (Exception e) {
            debugInfo.put("error", e.getMessage());
            log.error("Error in revenue debug endpoint", e);
        }
        
        return ResponseEntity.ok(debugInfo);
    }
}
