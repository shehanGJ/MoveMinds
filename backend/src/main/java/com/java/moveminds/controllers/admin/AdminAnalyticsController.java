package com.java.moveminds.controllers.admin;

import com.java.moveminds.dto.response.admin.*;
import com.java.moveminds.services.admin.AdminAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;

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
}
