package com.java.moveminds.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.java.moveminds.dto.requests.UpdateUserRoleRequest;
import com.java.moveminds.dto.response.AdminUserResponse;
import com.java.moveminds.dto.response.AdminStatsResponse;
import com.java.moveminds.enums.Roles;
import com.java.moveminds.services.AdminService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {
    
    private final AdminService adminService;

    /**
     * Get admin dashboard statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getAdminStats(Principal principal) {
        AdminStatsResponse stats = adminService.getAdminStats(principal);
        return ResponseEntity.ok(stats);
    }

    /**
     * Get all users with pagination and filtering
     */
    @GetMapping("/users")
    public ResponseEntity<Page<AdminUserResponse>> getAllUsers(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "role", required = false) Roles role,
            @RequestParam(value = "search", required = false) String search,
            Principal principal) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AdminUserResponse> users = adminService.getAllUsers(principal, pageable, role, search);
        return ResponseEntity.ok(users);
    }

    /**
     * Get user details by ID
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<AdminUserResponse> getUserById(
            @PathVariable Integer userId,
            Principal principal) {
        AdminUserResponse user = adminService.getUserById(principal, userId);
        return ResponseEntity.ok(user);
    }

    /**
     * Update user role
     */
    @PutMapping("/users/{userId}/role")
    public ResponseEntity<AdminUserResponse> updateUserRole(
            @PathVariable Integer userId,
            @RequestBody UpdateUserRoleRequest request,
            Principal principal) {
        AdminUserResponse updatedUser = adminService.updateUserRole(principal, userId, request.getRole());
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Activate/Deactivate user account
     */
    @PutMapping("/users/{userId}/status")
    public ResponseEntity<AdminUserResponse> updateUserStatus(
            @PathVariable Integer userId,
            @RequestParam boolean active,
            Principal principal) {
        AdminUserResponse updatedUser = adminService.updateUserStatus(principal, userId, active);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Delete user account
     */
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Integer userId,
            Principal principal) {
        adminService.deleteUser(principal, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get all instructors
     */
    @GetMapping("/instructors")
    public ResponseEntity<List<AdminUserResponse>> getAllInstructors(Principal principal) {
        List<AdminUserResponse> instructors = adminService.getAllInstructors(principal);
        return ResponseEntity.ok(instructors);
    }

    /**
     * Promote user to instructor
     */
    @PostMapping("/users/{userId}/promote-instructor")
    public ResponseEntity<AdminUserResponse> promoteToInstructor(
            @PathVariable Integer userId,
            Principal principal) {
        AdminUserResponse updatedUser = adminService.promoteToInstructor(principal, userId);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Demote instructor to regular user
     */
    @PostMapping("/users/{userId}/demote-instructor")
    public ResponseEntity<AdminUserResponse> demoteFromInstructor(
            @PathVariable Integer userId,
            Principal principal) {
        AdminUserResponse updatedUser = adminService.demoteFromInstructor(principal, userId);
        return ResponseEntity.ok(updatedUser);
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
