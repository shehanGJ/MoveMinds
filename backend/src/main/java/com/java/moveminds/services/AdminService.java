package com.java.moveminds.services;

import com.java.moveminds.dto.requests.UpdateUserRoleRequest;
import com.java.moveminds.dto.response.AdminStatsResponse;
import com.java.moveminds.dto.response.AdminUserResponse;
import com.java.moveminds.enums.Roles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.security.Principal;
import java.util.List;

public interface AdminService {
    AdminStatsResponse getAdminStats(Principal principal);
    Page<AdminUserResponse> getAllUsers(Principal principal, Pageable pageable, Roles role, String search);
    AdminUserResponse getUserById(Principal principal, Integer userId);
    AdminUserResponse updateUserRole(Principal principal, Integer userId, Roles role);
    AdminUserResponse updateUserStatus(Principal principal, Integer userId, boolean active);
    void deleteUser(Principal principal, Integer userId);
    List<AdminUserResponse> getAllInstructors(Principal principal);
    AdminUserResponse promoteToInstructor(Principal principal, Integer userId);
    AdminUserResponse demoteFromInstructor(Principal principal, Integer userId);
    List<String> getSystemLogs(Principal principal, int limit);
}
