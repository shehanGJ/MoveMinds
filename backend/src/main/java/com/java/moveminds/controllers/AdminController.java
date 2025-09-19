package com.java.moveminds.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.java.moveminds.dto.response.AdminStatsResponse;
import com.java.moveminds.services.AdminService;
import com.java.moveminds.services.admin.AdminUserManagementService;
import com.java.moveminds.services.admin.AdminSystemManagementService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {
    
    private final AdminService adminService;
    private final AdminUserManagementService adminUserManagementService;
    private final AdminSystemManagementService adminSystemManagementService;

    /**
     * Get admin dashboard statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getAdminStats(Principal principal) {
        AdminStatsResponse stats = adminService.getAdminStats(principal);
        return ResponseEntity.ok(stats);
    }


    /**
     * Get system logs
     */
    @GetMapping("/logs")
    public ResponseEntity<List<String>> getSystemLogs(
            @RequestParam(value = "limit", defaultValue = "100") int limit,
            Principal principal) {
        List<String> logs = adminService.getSystemLogs(principal, limit);
        return ResponseEntity.ok(logs);
    }
}
