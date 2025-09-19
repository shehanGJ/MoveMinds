package com.java.moveminds.controllers.admin;

import com.java.moveminds.dto.response.admin.AdminDashboardResponse;
import com.java.moveminds.dto.response.AdminStatsResponse;
import com.java.moveminds.services.admin.AdminSystemManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for admin system management operations.
 * Handles system-wide operations, analytics, and administrative functions.
 */
@Slf4j
@RestController
@RequestMapping("/admin/system")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminSystemManagementController {
    
    private final AdminSystemManagementService adminSystemManagementService;
    
    /**
     * Get comprehensive admin dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardResponse> getAdminDashboard(Principal principal) {
        
        log.info("Admin {} requesting dashboard data", principal.getName());
        
        AdminDashboardResponse dashboard = adminSystemManagementService.getAdminDashboard(principal);
        return ResponseEntity.ok(dashboard);
    }
    
    /**
     * Get basic admin statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getAdminStats(Principal principal) {
        
        log.info("Admin {} requesting basic statistics", principal.getName());
        
        AdminStatsResponse stats = adminSystemManagementService.getAdminStats(principal);
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Get system health status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getSystemHealth(Principal principal) {
        
        log.info("Admin {} checking system health", principal.getName());
        
        Map<String, Object> health = adminSystemManagementService.getSystemHealth(principal);
        return ResponseEntity.ok(health);
    }
    
    /**
     * Get system logs
     */
    @GetMapping("/logs")
    public ResponseEntity<List<String>> getSystemLogs(
            @RequestParam(value = "limit", defaultValue = "100") int limit,
            @RequestParam(value = "level", required = false) String level,
            @RequestParam(value = "category", required = false) String category,
            Principal principal) {
        
        log.info("Admin {} requesting system logs: limit={}, level={}, category={}", 
                principal.getName(), limit, level, category);
        
        List<String> logs = adminSystemManagementService.getSystemLogs(principal, limit, level, category);
        return ResponseEntity.ok(logs);
    }
    
    /**
     * Clear old system logs
     */
    @DeleteMapping("/logs")
    public ResponseEntity<Void> clearOldLogs(
            @RequestParam int daysOld,
            Principal principal) {
        
        log.info("Admin {} clearing logs older than {} days", principal.getName(), daysOld);
        
        adminSystemManagementService.clearOldLogs(principal, daysOld);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Get database statistics
     */
    @GetMapping("/database/stats")
    public ResponseEntity<Map<String, Object>> getDatabaseStatistics(Principal principal) {
        
        log.info("Admin {} requesting database statistics", principal.getName());
        
        Map<String, Object> stats = adminSystemManagementService.getDatabaseStatistics(principal);
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Perform database maintenance
     */
    @PostMapping("/database/maintenance")
    public ResponseEntity<Void> performDatabaseMaintenance(Principal principal) {
        
        log.info("Admin {} performing database maintenance", principal.getName());
        
        adminSystemManagementService.performDatabaseMaintenance(principal);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Get system configuration
     */
    @GetMapping("/configuration")
    public ResponseEntity<Map<String, Object>> getSystemConfiguration(Principal principal) {
        
        log.info("Admin {} requesting system configuration", principal.getName());
        
        Map<String, Object> configuration = adminSystemManagementService.getSystemConfiguration(principal);
        return ResponseEntity.ok(configuration);
    }
    
    /**
     * Update system configuration
     */
    @PutMapping("/configuration")
    public ResponseEntity<Void> updateSystemConfiguration(
            @RequestBody Map<String, Object> configuration,
            Principal principal) {
        
        log.info("Admin {} updating system configuration", principal.getName());
        
        adminSystemManagementService.updateSystemConfiguration(principal, configuration);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Get system alerts
     */
    @GetMapping("/alerts")
    public ResponseEntity<List<AdminDashboardResponse.SystemAlert>> getSystemAlerts(Principal principal) {
        
        log.info("Admin {} requesting system alerts", principal.getName());
        
        List<AdminDashboardResponse.SystemAlert> alerts = adminSystemManagementService.getSystemAlerts(principal);
        return ResponseEntity.ok(alerts);
    }
    
    /**
     * Resolve system alert
     */
    @PutMapping("/alerts/{alertId}/resolve")
    public ResponseEntity<Void> resolveSystemAlert(
            @PathVariable String alertId,
            Principal principal) {
        
        log.info("Admin {} resolving alert: {}", principal.getName(), alertId);
        
        adminSystemManagementService.resolveSystemAlert(principal, alertId);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Get backup status
     */
    @GetMapping("/backup/status")
    public ResponseEntity<Map<String, Object>> getBackupStatus(Principal principal) {
        
        log.info("Admin {} checking backup status", principal.getName());
        
        Map<String, Object> status = adminSystemManagementService.getBackupStatus(principal);
        return ResponseEntity.ok(status);
    }
    
    /**
     * Create system backup
     */
    @PostMapping("/backup/create")
    public ResponseEntity<Void> createSystemBackup(Principal principal) {
        
        log.info("Admin {} creating system backup", principal.getName());
        
        adminSystemManagementService.createSystemBackup(principal);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Restore system from backup
     */
    @PostMapping("/backup/restore/{backupId}")
    public ResponseEntity<Void> restoreSystemFromBackup(
            @PathVariable String backupId,
            Principal principal) {
        
        log.info("Admin {} restoring system from backup: {}", principal.getName(), backupId);
        
        adminSystemManagementService.restoreSystemFromBackup(principal, backupId);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Get performance metrics
     */
    @GetMapping("/performance")
    public ResponseEntity<Map<String, Object>> getPerformanceMetrics(Principal principal) {
        
        log.info("Admin {} requesting performance metrics", principal.getName());
        
        Map<String, Object> metrics = adminSystemManagementService.getPerformanceMetrics(principal);
        return ResponseEntity.ok(metrics);
    }
    
    /**
     * Get user activity analytics
     */
    @GetMapping("/analytics/user-activity")
    public ResponseEntity<Map<String, Object>> getUserActivityAnalytics(
            @RequestParam(defaultValue = "30d") String period,
            Principal principal) {
        
        log.info("Admin {} requesting user activity analytics for period: {}", principal.getName(), period);
        
        Map<String, Object> analytics = adminSystemManagementService.getUserActivityAnalytics(principal, period);
        return ResponseEntity.ok(analytics);
    }
    
    /**
     * Get revenue analytics
     */
    @GetMapping("/analytics/revenue")
    public ResponseEntity<Map<String, Object>> getRevenueAnalytics(
            @RequestParam(defaultValue = "30d") String period,
            Principal principal) {
        
        log.info("Admin {} requesting revenue analytics for period: {}", principal.getName(), period);
        
        Map<String, Object> analytics = adminSystemManagementService.getRevenueAnalytics(principal, period);
        return ResponseEntity.ok(analytics);
    }
    
    /**
     * Get program analytics
     */
    @GetMapping("/analytics/programs")
    public ResponseEntity<Map<String, Object>> getProgramAnalytics(
            @RequestParam(defaultValue = "30d") String period,
            Principal principal) {
        
        log.info("Admin {} requesting program analytics for period: {}", principal.getName(), period);
        
        Map<String, Object> analytics = adminSystemManagementService.getProgramAnalytics(principal, period);
        return ResponseEntity.ok(analytics);
    }
    
    /**
     * Send system-wide notification
     */
    @PostMapping("/notifications/send")
    public ResponseEntity<Void> sendSystemNotification(
            @RequestParam String message,
            @RequestParam String type,
            Principal principal) {
        
        log.info("Admin {} sending system notification: type={}", principal.getName(), type);
        
        adminSystemManagementService.sendSystemNotification(principal, message, type);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Get system audit trail
     */
    @GetMapping("/audit-trail")
    public ResponseEntity<List<Object>> getSystemAuditTrail(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String user,
            Principal principal) {
        
        log.info("Admin {} requesting audit trail: action={}, user={}", principal.getName(), action, user);
        
        List<Object> auditTrail = adminSystemManagementService.getSystemAuditTrail(principal, action, user);
        return ResponseEntity.ok(auditTrail);
    }
}
